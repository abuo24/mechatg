package mechat.group.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseFile {
  private String name;
  private String url;
  private String type;
  private long size;
}
