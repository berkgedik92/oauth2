package project.diagram.safeAPI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import project.diagram.security.model.CurrentUser;
import project.diagram.security.model.UserPrincipal;

@RestController
@RequestMapping("api")
public class DummyController {

  @RequestMapping(value = "/hello", method = RequestMethod.GET)
  @ResponseBody
  @CrossOrigin
  public ResponseEntity<String> save(@CurrentUser UserPrincipal userPrincipal) {
    return new ResponseEntity<>("Hello " + userPrincipal.getName(), HttpStatus.OK);
  }
}
