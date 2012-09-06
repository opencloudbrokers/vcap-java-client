package org.cloudfoundry.client.lib.util;


import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.springframework.http.client.CommonsClientHttpRequestFactory;

import java.util.HashMap;
import java.util.Map;


/*
 * Allows selectively overriding of the names that we can refer to remote cloud foundry instances as.
 * Eg, if a CF instance is running on 111.111.111.111, and calls itself simple.cloud, we can add an override here
 * for simplecloud/ 111.111.111.111.  From then on, we can refer to X.simple.cloud in API calls, and that will be handled
 * correctly.
 *
 * This eases automated testing concerns when updating of DNS or hosts files is not desirable.
 */
public class CloudFoundryHostCommonsHttpRequestFactory extends CommonsClientHttpRequestFactory {

  private static Map<String, String> virtualHostToIP = new HashMap<String, String>();

  public static void addIpToCoudFoundryNameMapping(String ipaddress, String cloudFoundryName) {
    virtualHostToIP.put(cloudFoundryName, ipaddress);
  }

  @Override
  public void postProcessCommonsHttpMethod(HttpMethodBase httpMethod) {
    try {
      String cloudFoundryName = getCloudFoundryName(httpMethod.getURI());
      if (virtualHostToIP.get(cloudFoundryName) != null) {
        String ip = virtualHostToIP.get(cloudFoundryName);
        httpMethod.getParams().setVirtualHost(httpMethod.getURI().getAuthority());
        URI uri = httpMethod.getURI();
        uri.setEscapedAuthority(ip);
        httpMethod.setURI(uri);
      }
    } catch (URIException e) {
      e.printStackTrace();
    }
  }

  static String getCloudFoundryName(URI uri) throws URIException {
    String name = uri.getAuthority();
    if (!name.contains(".")) {
      return name;
    }
    return name.substring(name.indexOf(".") + 1);
  }
}
