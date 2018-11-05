package service;

import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class IDGeneratorServiceImpl implements IDGeneratorService {

  @Override
  public String generateId(String url) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(url.getBytes());
      byte[] digest = md5.digest();
      return DatatypeConverter.printHexBinary(digest).toLowerCase();
    } catch (NoSuchAlgorithmException e) {
      StringBuffer buf = new StringBuffer();
      for (int i=1; i<=32; i++) {
        buf.append((char)(65 + (int) (Math.random() * 25)));
      }
      return buf.toString();
    }
  }
}
