package khaale.archiunit.infrastructure.impl;

import khaale.archiunit.infrastructure.GraphSource;
import com.opencsv.CSVReader;
import lombok.SneakyThrows;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.io.FileReader;
import java.nio.file.Paths;

public class CsvGraphSource implements GraphSource {

  private final String csvPath;

  public CsvGraphSource(String csvPath) {
    this.csvPath = csvPath;
  }

  @Override
  @SneakyThrows
  public Graph load() {

    Graph graph = TinkerGraph.open();

    String elementsPath = Paths.get(csvPath, "elements.csv").toString();
    try (CSVReader csvReader = new CSVReader(new FileReader(elementsPath))) {
      String[] values;
      csvReader.skip(1);
      while ((values = csvReader.readNext()) != null) {
        String id = values[0];
        String type = values[1];
        String name = values[2];

        graph.addVertex(T.id, id, T.label, type, "name", name);
      }
    }

    String relationsPath = Paths.get(csvPath, "relations.csv").toString();
    try (CSVReader csvReader = new CSVReader(new FileReader(relationsPath))) {
      String[] values;
      csvReader.skip(1);
      while ((values = csvReader.readNext()) != null) {
        String relId = values[0];
        String relType = values[1];
        String srcId = values[4];
        String dstId = values[5];

        Vertex srcV = graph.vertices(srcId).next();
        Vertex dstV = graph.vertices(dstId).next();

        srcV.addEdge(relType, dstV, T.id, relId);
      }
    }

    return graph;
  }
}
