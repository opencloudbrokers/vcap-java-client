package org.cloudfoundry.client.lib.util;

import org.apache.commons.httpclient.HttpMethodBase;
import static org.mockito.Mockito.*;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Test;
import static junit.framework.TestCase.*;

public class CloudFoundryHostCommonsHttpRequestFactoryTest {

  @Test
  public void whenIpAddedUriIsUpdated() throws URIException {
    HttpMethodBase method = mock(HttpMethodBase.class);

    CloudFoundryHostCommonsHttpRequestFactory factory = new CloudFoundryHostCommonsHttpRequestFactory();
    HttpMethodParams params = new HttpMethodParams();

    CloudFoundryHostCommonsHttpRequestFactory.addIpToCoudFoundryNameMapping("111.111.111.111", "somecloud");

    when(method.getURI()).thenReturn(new URI("http://api.somecloud/apps", false));
    when(method.getParams()).thenReturn(params);

    factory.postProcessCommonsHttpMethod(method);

    verify(method).setURI(new URI("http://111.111.111.111/apps"));

    assertEquals("api.somecloud", params.getVirtualHost());
  }

  @Test
  public void whenNoCloudNameMatchDoNothing() throws URIException {
    HttpMethodBase method = mock(HttpMethodBase.class);

    CloudFoundryHostCommonsHttpRequestFactory factory = new CloudFoundryHostCommonsHttpRequestFactory();
    HttpMethodParams params = new HttpMethodParams();

    when(method.getURI()).thenReturn(new URI("http://api.somecloud/apps", false));
    when(method.getParams()).thenReturn(params);

    factory.postProcessCommonsHttpMethod(method);

    verify(method, never()).setURI((URI) any());

    assertEquals(null, params.getVirtualHost());
  }
}
