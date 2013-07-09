package org.motechproject.verboicesandbox.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;

import org.motechproject.verboicesandbox.ivr.CallMaker;
    
@Controller
public class InitiateCallController {
    
    @Autowired
    private CallMaker callMaker;

    @RequestMapping("/makeCall")
    @ResponseBody
    public String makeCall() {
        // display summary of initiated call in browser
        String summary = callMaker.makeCall();
        return summary;
    }
}
