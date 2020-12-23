package mechat.group.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SmsPayload {

    @NotNull
    private final String phoneNumber;
    @NotNull
    private final String message;
}
