package khaale.archiunit;

import khaale.archiunit.infrastructure.GraphSource;
import khaale.archiunit.infrastructure.impl.CsvGraphSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;


public class GraphTest {

  private static Graph graph;

  @BeforeClass
  public static void beforeAll() {
    GraphSource graphSource = new CsvGraphSource("./src/test/resources/examples/csv/example-model/");
    graph = graphSource.load();
  }

  @Test
  public void graph_shouldBe_loaded() {
    Assertions.assertNotNull(graph);
    Assertions.assertTrue(graph.traversal().V().count().next() > 0);
    Assertions.assertTrue(graph.traversal().E().count().next() > 0);
  }

  @Test
  public void businessServices_mustBe_realizedBy_applicationService() {
    List<Object> result = graph.traversal()
        .V()
        .hasLabel(VLabel.BusinessService.name())
        .where(
            not(
                or(
                    // should be realized by application service
                    inE().hasLabel(ELabel.RealizationRelationship.name())
                        .outV().hasLabel(VLabel.ApplicationService.name()),
                    // or group
                    inE().hasLabel(ELabel.RealizationRelationship.name())
                        .outV().hasLabel(VLabel.Grouping.name())
                    )
            ))
        .values("name")
        .dedup()
        .order()
        .toList();

    assertEmptyCollection(result, "Business Service is not realized by Application Service");
  }

  @Test
  public void applicationServices_must_realize_businessService_or_specialize_or_beAPartOf_anotherApplicationService() {
    List<Object> result = graph.traversal()
        .V()
        .hasLabel(VLabel.ApplicationService.name())
        .where(
            not(
                or(
                    // should realize a business service
                    outE().hasLabel(ELabel.RealizationRelationship.name())
                        .inV().hasLabel(VLabel.BusinessService.name()),
                    // should specialize another application service
                    outE().hasLabel(ELabel.SpecializationRelationship.name())
                        .inV().hasLabel(VLabel.ApplicationService.name()),
                    // should be composed or aggregated in another application service
                    inE().hasLabel(ELabel.AggregationRelationship.name(), ELabel.CompositionRelationship.name())
                        .outV().hasLabel(VLabel.ApplicationService.name()))
            ))
        .values("name")
        .dedup()
        .order()
        .toList();

    assertEmptyCollection(result, "Application Service is not realize, specialize or be composed");
  }


  private void assertEmptyCollection(List<?> collection, String message) {
    Assertions.assertEquals(0, collection.size(),
        message + ": \n" +
            collection.stream().map(Object::toString).collect(Collectors.joining("\n"))
            + "\n" );
  }

  private void assertNotEmptyCollection(List<?> collection, String message) {
    Assertions.assertTrue(collection.size() > 0,
        message + ": \n" +
            collection.stream().map(Object::toString).collect(Collectors.joining("\n"))
            + "\n" );
  }
}

enum VLabel {
  Grouping,
  Product,
  BusinessCollaboration,
  BusinessService,
  BusinessFunction,
  ApplicationCollaboration,
  ApplicationService,
  ApplicationFunction,
  ApplicationComponent,
  ApplicationInterface
}

enum ELabel {
  AssignmentRelationship,
  AggregationRelationship,
  CompositionRelationship,
  SpecializationRelationship,
  ServingRelationship,
  RealizationRelationship,
}