module com.BayWave.baywave {
    requires com.h2database;
    requires pencil.password.encoder;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires jdk.httpserver;
    requires com.google.gson;
    exports com.BayWave.Triggers;
}