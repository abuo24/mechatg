package mechat.group.repository;

import mechat.group.vm.SmsPayload;

public interface SmsSender {

    void sendSms(SmsPayload smsRequest);

    // or maybe void sendSms(String phoneNumber, String message);
}
