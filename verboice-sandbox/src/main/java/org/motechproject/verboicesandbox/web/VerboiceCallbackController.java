package org.motechproject.verboicesandbox.web;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.verboicesandbox.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/web-api")
public class VerboiceCallbackController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SettingsFacade settingsFacade;

    /**
     * Serve the Verboice external services manifest.
     * 
     * Reads a template manifest from the "raw" configuration folder on the
     * Motech server. This folder is located at a path like
     * ~/.motech/config/bundle-name/raw. Inserts the base URL for the server
     * into callback URLs contained in the manifest text.
     * 
     * @param request
     *            the HTTP request, parameters, etc.
     * 
     * @return XML string of the external services manifest
     */
    @RequestMapping("/manifest")
    @ResponseBody
    public String manifest(HttpServletRequest request) throws IOException {
        logger.info("Serving Manifest.");

        String manifestFileName = settingsFacade
                .getProperty("verboice.manifest.file");
        InputStream manifest = settingsFacade.getRawConfig(manifestFileName);
        String manifestBody = IOUtils.toString(manifest);

        String callbackBaseUrl = settingsFacade.getProperty("motech.base.url")
                + settingsFacade.getProperty("vervoice.callback.path");

        return manifestBody.replace("URL_BASE", callbackBaseUrl).replace(
                "EXTRA_PAYLOAD", Constants.CUSTOM_PAYLOAD);
    }

    /**
     * Validate the pin that the user entered in their phone.
     * 
     * Compares the pin that the user entered into their phone to the expected
     * pin stored in a properties file on the Motech server. The properties file
     * is located at a path like ~/.motech/config/bundle-name. The Verboice
     * callflow has a "pin" variable, which Verboice exposes as a HTTP request
     * parameter.
     * 
     * @param request
     *            the HTTP request, parameters, etc.
     * 
     * @return JSON object with "checkPinResult" key: true if the attempted pin
     *         matches the stored pin. Verboice can make thiss available as a
     *         call flow variable.
     */
    @ResponseBody
    @RequestMapping("/checkPin")
    public String checkPin(HttpServletRequest request) {
        logger.info("Checking Pin");

        String attemptedPin = request.getParameter("pin");
        String userPin = settingsFacade.getProperty("user.pin");
        logger.info("attemptedPin: " + attemptedPin + " userPin: " + userPin);

        if (StringUtils.isNotBlank(attemptedPin)
                && attemptedPin.equals(userPin)) {
            logger.info("Correct pin.");
            return "{\"checkPinResult\": \"true\"}";
        } else {
            logger.info("Wrong pin.");
            return "{\"checkPinResult\": \"false\"}";
        }
    }

    /**
     * Log various Verboice call flow variables.
     * 
     * Verboice has some custom call flow variables like "pin" and
     * "isCorrectPin". It also has some other call-related variables like the
     * Verboice call Id. Verboice exposes many of these as HTTP request
     * parameters. Read them all and write them to the log.
     * 
     * @param request
     *            the HTTP request, parameters, etc.
     * 
     * @return JSON object with "reportDataResult" key: always true. Verboice
     *         can make this available as a call flow variable.
     */
    @ResponseBody
    @RequestMapping("/reportData")
    public String reportData(HttpServletRequest request) {
        String callData = "Call Flow Data: ";
        Map<String, String[]> results = request.getParameterMap();
        for (String name : results.keySet()) {
            callData += name + "={";
            for (String value : results.get(name)) {
                callData += value + " ";
            }
            callData += "}";
        }
        logger.info(callData);

        return "{\"reportDataResult\": \"true\"}";
    }
}
