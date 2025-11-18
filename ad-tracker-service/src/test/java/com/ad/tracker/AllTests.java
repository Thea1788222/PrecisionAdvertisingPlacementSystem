package com.ad.tracker;

import com.ad.tracker.controller.AdRecommendationControllerTest;
import com.ad.tracker.controller.TrackControllerTest;
import com.ad.tracker.controller.UserProfileControllerTest;
import com.ad.tracker.service.AdImpressionServiceTest;
import com.ad.tracker.service.AdRecommendationServiceTest;
import com.ad.tracker.service.UserBehaviorServiceTest;
import com.ad.tracker.service.UserProfileServiceTest;
import com.ad.tracker.integration.AdTrackerIntegrationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    AdRecommendationControllerTest.class,
    TrackControllerTest.class,
    UserProfileControllerTest.class,
    AdImpressionServiceTest.class,
    AdRecommendationServiceTest.class,
    UserBehaviorServiceTest.class,
    UserProfileServiceTest.class,
    AdTrackerIntegrationTest.class
})
public class AllTests {
}