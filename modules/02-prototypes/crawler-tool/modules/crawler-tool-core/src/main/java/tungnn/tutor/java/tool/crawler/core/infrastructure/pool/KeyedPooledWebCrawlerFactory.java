package tungnn.tutor.java.tool.crawler.core.infrastructure.pool;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import tungnn.tutor.java.selenium.pool.WebDriverPool;
import tungnn.tutor.java.tool.crawler.core.domain.AbstractWebCrawler;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawler;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawlerType;

public class KeyedPooledWebCrawlerFactory
    extends BaseKeyedPooledObjectFactory<WebCrawlerType, WebCrawler> {

  private final WebDriverPool driverPool;

  public KeyedPooledWebCrawlerFactory(WebDriverPool driverPool) {
    this.driverPool = driverPool;
  }

  @Override
  public WebCrawler create(WebCrawlerType key) throws Exception {
    var driver = driverPool.borrowDriver();
    return WebCrawlerFactory.create(key, driver);
  }

  @Override
  public PooledObject<WebCrawler> wrap(WebCrawler value) {
    return new DefaultPooledObject<>(value);
  }

  @Override
  public void destroyObject(WebCrawlerType key, PooledObject<WebCrawler> p) {
    var pageCrawler = p.getObject();
    if (pageCrawler instanceof AbstractWebCrawler abstractWebCrawler) {
      var driver = abstractWebCrawler.driver();
      if (driver != null) {
        driverPool.returnDriver(driver);
      }
    }
  }
}
