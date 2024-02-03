package io.plotnik.entran

import org.ccil.cowan.tagsoup.Parser
import groovy.xml.XmlParser
import groovy.yaml.YamlSlurper
import groovy.json.JsonSlurper
import groovy.sql.Sql

class TranEngine {
    
    String databaseName;
    
    def sql;

    int enPos = 1;
    int ruPos = 1;

    List enPar;
    List ruPar;

    static boolean verbose = Main.verbose;
    
    TranEngine(String databaseName, String enFile, String ruFile) {

        this.databaseName = databaseName
        
        // Open database

        def url = 'jdbc:sqlite:' + databaseName + '.tran.db'
        def user = ''
        def password = ''
        def driver = 'org.sqlite.JDBC'

        sql = Sql.newInstance(url, user, password, driver)

        // Read CSS classes for paragraphs
        
        String configFile = databaseName + ".yml"
        YamlSlurper yamlSlurper = new YamlSlurper()
        
        def config = yamlSlurper.parseText(new File(configFile).text)
        
        // Parse HTML
        
        XmlParser parser = new XmlParser(new Parser())
        
        def enTree = parser.parseText(new File(enFile).text)
        def ruTree = parser.parseText(new File(ruFile).text)

        enPar = extractParagraphs(enTree, config.paragraphs.en)
        if (verbose) {
            println "enPar size: ${enPar.size()}"
        }

        ruPar = extractParagraphs(ruTree, config.paragraphs.ru)
        if (verbose) {
            println "ruPar size: ${ruPar.size()}"
        }
        
        readStateFile()
    }
    
    void initializeDatabase() {
        sql.execute '''
        drop table if exists Tran;
        '''
        sql.execute '''
        create table Tran (id integer primary key, en string, ru string);
        '''

        println "Initializing database ${databaseName}..."
        int maxSize = Math.max(enPar.size(), ruPar.size());
        for (int i=1; i<=maxSize; i++) {
            String en = i>enPar.size()? "": enPar[i-1];
            String ru = i>ruPar.size()? "": ruPar[i-1];
            sql.execute """
		    insert into Tran (id, en, ru) values ($i, $en, $ru);
		    """	
        }
        println "Done"
    }

    List extractParagraphs(tree, para) {
        List result = []
        tree.'**'.each { em -> 
            if (em instanceof Node) {
                String c = em.@class 
                if (para.contains(c)) {
                    result << em.text()
                }
            }
        }
        return result
    }
    
    Sentence findParagraphEn() {
        return new Sentence(enPos, enPar[enPos-1])
    }

    Sentence findParagraphRu() {
        return new Sentence(ruPos, ruPar[ruPos-1])
    }

    String getRuText() {
        def res = sql.firstRow "select ru from Tran where id=${ruPos}"
        return res?.ru
    }

    // Read positions from state file
    void readStateFile() {
        String stateFileName = databaseName + ".json"
        File stateFile = new File(stateFileName)
        if (stateFile.exists()) {
            json = new JsonSlurper().parseText(stateFile.text)
            enPos = json.en
            ruPos = json.ru
        } 
    }
    
    void writeStateFile() {
        String stateFileName = databaseName + ".json"
        new File(stateFileName).text = JsonOutput.toJson(["en":enPos, "ru":ruPos])
    }
}

