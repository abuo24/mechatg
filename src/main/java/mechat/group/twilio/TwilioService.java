package mechat.group.twilio;

import mechat.group.repository.SmsSender;
import mechat.group.vm.SmsPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.springframework.stereotype.Service
public class TwilioService {

    private final SmsSender smsSender;

    @Autowired
    public TwilioService(@Qualifier("twilio") TwilioSmsSender smsSender) {
        this.smsSender = smsSender;
    }

    public void sendSms(SmsPayload smsRequest) {
        smsSender.sendSms(smsRequest);
    }
}
