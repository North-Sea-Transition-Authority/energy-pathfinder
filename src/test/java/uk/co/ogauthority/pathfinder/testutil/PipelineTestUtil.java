package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.pipeline.Pipeline;

public class PipelineTestUtil {

  public static final Integer PIPELINE_ID = 1;
  public static final String PIPELINE_NAME = "PIPE1";

  public static Pipeline getPipeline() {
    return getPipeline(
        PIPELINE_ID,
        PIPELINE_NAME
    );
  }

  public static Pipeline getPipeline(Integer id, String name) {
    return new Pipeline(
        id,
        name
    );
  }
}
