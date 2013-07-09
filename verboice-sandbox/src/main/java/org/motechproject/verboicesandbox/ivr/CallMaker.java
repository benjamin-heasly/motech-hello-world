package org.motechproject.verboicesandbox.ivr;

import java.util.Map;

import org.motechproject.server.config.SettingsFacade;
import org.motechproject.verboicesandbox.constants.Constants;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallMaker {

    private final IVRService ivrService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SettingsFacade settingsFacade;

    @Autowired
    public CallMaker(IVRService ivrService) {
        this.ivrService = ivrService;
    }

    public String makeCall() {

        // build an ivr call request
        int callTimeoutSecs;
        try {
            String callTimeoutStr = settingsFacade.getProperty("ivr.timeout");
            callTimeoutSecs = Integer.parseInt(callTimeoutStr);
        } catch (NumberFormatException e) {
            callTimeoutSecs = 120;
        }
        String phoneNum = settingsFacade.getProperty("ivr.phone.number");
        String channelName = settingsFacade.getProperty("ivr.channel.name");
        CallRequest callRequest = new CallRequest(phoneNum, callTimeoutSecs,
                channelName);

        // build call request payload
        // note Verboice call flow id
        // note status callback URL which hits the ivr-verboice module
        String userId = settingsFacade.getProperty("user.Id");
        String callFlowId = settingsFacade.getProperty("verboice.callflow.Id");
        String extraPayload = settingsFacade.getProperty("ivr.extra.payload");
        String statusCallbackUrl = settingsFacade
                .getProperty("motech.base.url")
                + settingsFacade.getProperty("motech.ivr.status.path");
        Map<String, String> payload = callRequest.getPayload();
        payload.put(Constants.USER_ID, userId);
        payload.put(Constants.CUSTOM_PAYLOAD, extraPayload);
        payload.put(Constants.FLOW_ID, callFlowId);
        payload.put(Constants.STATUS_CALLBACK_URL, statusCallbackUrl);

        // place the call
        String summary = "Initiating call to: " + phoneNum + " on channel: "
                + channelName + " for call flow " + callFlowId;
        logger.info(summary);
        ivrService.initiateCall(callRequest);

        // let caller display this summary
        return (summary);
    }
}
