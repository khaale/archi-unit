package khaale.archiunit.infrastructure;

import org.apache.tinkerpop.gremlin.structure.Graph;

public interface GraphSource
{
  Graph load();
}
