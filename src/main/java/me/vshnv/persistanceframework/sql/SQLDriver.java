package me.vshnv.persistanceframework.sql;

public enum SQLDriver {
    MYSQL("mysql","com.mysql.jdbc.Driver"),
    MSSQL("mssql","com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    MARIADB("mariadb","org.mariadb.jdbc.Driver"),
    POSTGRE("postgre","org.postgresql.Driver"),
    SQLITE("sqlite", "org.sqlite.JDBC","jdbc:%1$s:%2$s");

    private String driverName;
    private String driverClasspath;
    private String format;


    private static final String STANDARD_URL_FORMAT = "jdbc:%1$s://%2$s:%3$s/%4$s";
    SQLDriver(String driverName, String driverClasspath, String format){
        this.driverName = driverName;
        this.driverClasspath = driverClasspath;
        this.format = format;
    }
    SQLDriver(String driverName, String driverClasspath){
        this.driverName = driverName;
        this.driverClasspath = driverClasspath;
        this.format = STANDARD_URL_FORMAT;
    }

    public void loadDriver() throws ConnectorDriverNotFoundException{
        try {
            Class.forName(driverClasspath);
        } catch (ClassNotFoundException e) {
            throw new ConnectorDriverNotFoundException(driverClasspath);
        }
    }

    public String getDriverName() {
        return driverName;
    }

    public String getURLFormat(){
        return format;
    }
    public String getDriverClasspath(){
        return driverClasspath;
    }
}
