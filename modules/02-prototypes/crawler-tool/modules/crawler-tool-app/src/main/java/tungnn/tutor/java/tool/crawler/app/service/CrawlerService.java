package tungnn.tutor.java.tool.crawler.app.service;

import tungnn.tutor.java.tool.crawler.app.model.CrawlCourseRequest;
import tungnn.tutor.java.tool.crawler.app.model.CrawlCourseResult;

public interface CrawlerService {

  CrawlCourseResult crawlCourse(CrawlCourseRequest request);
}
