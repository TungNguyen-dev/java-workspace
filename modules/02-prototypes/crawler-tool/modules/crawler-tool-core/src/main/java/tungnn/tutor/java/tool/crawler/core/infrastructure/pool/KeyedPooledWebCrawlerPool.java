package tungnn.tutor.java.tool.crawler.core.infrastructure.pool;

import java.util.EnumMap;
import java.util.Map;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawler;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawlerType;

public class KeyedPooledWebCrawlerPool implements WebCrawlerPool {

  private final Map<WebCrawlerType, GenericObjectPool<WebCrawler>> pools =
      new EnumMap<>(WebCrawlerType.class);

  public KeyedPooledWebCrawlerPool(KeyedPooledObjectFactory<WebCrawlerType, WebCrawler> factory) {
    initKeyConfig(factory, WebCrawlerType.COURSERA, 1, 0, 0);
    initKeyConfig(factory, WebCrawlerType.YOUTUBE, 4, 2, 1);
    initKeyConfig(factory, WebCrawlerType.GENERIC, 4, 1, 0);
  }

  private void initKeyConfig(
      KeyedPooledObjectFactory<WebCrawlerType, WebCrawler> factory,
      WebCrawlerType type,
      int maxTotal,
      int maxIdle,
      int minIdle) {

    var config = new GenericObjectPoolConfig<WebCrawler>();
    config.setMaxTotal(maxTotal);
    config.setMaxIdle(maxIdle);
    config.setMinIdle(minIdle);
    config.setBlockWhenExhausted(true);

    PooledObjectFactory<WebCrawler> singleFactory =
        new PooledObjectFactory<>() {
          @Override
          public PooledObject<WebCrawler> makeObject() throws Exception {
            return factory.makeObject(type);
          }

          @Override
          public void destroyObject(PooledObject<WebCrawler> p) throws Exception {
            factory.destroyObject(type, p);
          }

          @Override
          public boolean validateObject(PooledObject<WebCrawler> p) {
            return factory.validateObject(type, p);
          }

          @Override
          public void activateObject(PooledObject<WebCrawler> p) throws Exception {
            factory.activateObject(type, p);
          }

          @Override
          public void passivateObject(PooledObject<WebCrawler> p) throws Exception {
            factory.passivateObject(type, p);
          }
        };

    pools.put(type, new GenericObjectPool<>(singleFactory, config));
  }

  @Override
  public WebCrawler borrowCrawler(WebCrawlerType type) {
    var pool = pools.get(type);
    if (pool == null) {
      pool = pools.get(WebCrawlerType.GENERIC);
    }
    try {
      return pool.borrowObject();
    } catch (Exception e) {
      throw new RuntimeException("Error borrowing WebCrawler from pool for type: " + type, e);
    }
  }

  @Override
  public void returnCrawler(WebCrawlerType type, WebCrawler webCrawler) {
    if (webCrawler != null) {
      var pool = pools.get(type);
      if (pool == null) {
        pool = pools.get(WebCrawlerType.GENERIC);
      }
      try {
        pool.returnObject(webCrawler);
      } catch (Exception e) {
        throw new RuntimeException("Error returning PageCrawler to pool for type: " + type, e);
      }
    }
  }

  @Override
  public void close() {
    for (var pool : pools.values()) {
      if (!pool.isClosed()) {
        pool.close();
      }
    }
  }
}
