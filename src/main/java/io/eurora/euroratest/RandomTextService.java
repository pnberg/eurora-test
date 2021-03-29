package io.eurora.euroratest;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RandomTextService {

  public String generateRandomText() {
    Lorem lorem = LoremIpsum.getInstance();
    return lorem.getWords(1, 10);
  }

}
