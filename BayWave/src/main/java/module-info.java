module com.BayWave.baywave {
    requires java.sql;
    requires com.h2database;
    requires pencil.password.encoder;
    exports com.BayWave.Triggers;
}