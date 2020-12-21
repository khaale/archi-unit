# archi-unit
Unit tests for Archi models (csv exported for now) using Gremlin.

Loads Archi model into embedded Gremlin engine, so model can be validated versus graph-based patterns.
As an example, currently tests that:
- Business Service must be realized by Application Service;
- Application Service must realize Business Service.
