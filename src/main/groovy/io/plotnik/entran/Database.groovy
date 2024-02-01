package io.plotnik.entran;

import groovy.sql.Sql

public class Database {

    def sql;

    public Database(String databaseName) {
        def url = 'jdbc:sqlite:' + databaseName + '.tran.db'
        def user = ''
        def password = ''
        def driver = 'org.sqlite.JDBC'

        sql = Sql.newInstance(url, user, password, driver)
    }

    public connect() {
        println "--- connect"
    }
       
}