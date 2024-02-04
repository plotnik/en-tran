package io.plotnik.entran

import org.ccil.cowan.tagsoup.Parser
import groovy.xml.XmlParser
import groovy.yaml.YamlSlurper
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.sql.Sql

import java.awt.event.*
import javax.swing.*

class TranEngine {
    
    String databaseName;
    
    def sql;

    int enPos = 1;
    int ruPos = 1;

    List enPar;
    List ruPar;

    String enLine;
    String ruLine;

    String stateFileName;

    EnTranFrame frame;

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
        
        stateFileName = databaseName + ".json"
        readStateFile()
    }
    
    void initDatabase() {
        String databaseFile = databaseName + '.tran.db'
        if (new File(databaseFile).exists() && !verbose) {
            throw new RuntimeException("Database already exists: " + databaseFile);
        }

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
        enLine = enPar[enPos-1];
        return new Sentence(enPos, enLine);
    }

    Sentence findParagraphRu() {
        ruLine = ruPar[ruPos-1];
        return new Sentence(ruPos, ruLine);
    }

    String getRuText() {
        def res = sql.firstRow "select ru from Tran where id=${ruPos}"
        return res?.ru
    }

    void storeTran(int k, String en, String ru) {
        sql.execute "replace into Tran (id, en, ru) values ($k, $en, $ru)"
    }

    String bold(String s) {
        return '<font size="+1">' + s.replace('<', '&lt;').replace('>', '&gt;') + '</font>'
    }

    String highlightKey(text, key, bracket) {
        if (text.startsWith(key)) {
            text = text.substring(key.length())
        }
        String br1 = ''
        String br2 = ''
        if (bracket=='>') {
            br2 = bold(key + ' ' + bracket)
        } else {
            br1 = bold(bracket + ' ' + key)
        }
        return '<html>' + br1 + ' ' + text + ' ' + br2
    }

    // Read positions from state file
    void readStateFile() {
        File stateFile = new File(stateFileName)
        if (stateFile.exists()) {
            def json = new JsonSlurper().parseText(stateFile.text)
            enPos = json.en
            ruPos = json.ru
        } 
    }
    
    void writeStateFile() {
        new File(stateFileName).text = JsonOutput.toJson(["en":enPos, "ru":ruPos])
    }

    void configureButtons() {

        configureButton(frame.prevButton, "F7", "<", {
            enPos--
            ruPos--
            frame.updateScreenText(true)
        })

        configureButton(frame.nextButton, "F8", ">", {
            storeTran(enPos, enLine, frame.tranTextArea.text)
            
            enPos++
            ruPos++
            new File(stateFileName).text = JsonOutput.toJson(["en":enPos, "ru":ruPos])
            frame.updateScreenText(true)
        })


        configureButton(frame.prevRuButton, "F9", "<", {
            if (ruPos > 1) {
                ruPos--
                frame.updateScreenText(false)
            }
        })

        configureButton(frame.nextRuButton, "F10", ">", {
            ruPos++
            frame.updateScreenText(false)
        })


        configureButton(frame.addButton, "F11", "+", {
            println "ruLine: $ruLine"
            frame.tranTextArea.text = (frame.tranTextArea.text + '\n' + wordbreak(ruLine)).trim() 
        })


        configureButton(frame.addNextButton, "F12", ">", {
            println "ruLine: $ruLine"
            frame.tranTextArea.text = (frame.tranTextArea.text + '\n' + wordbreak(ruLine)).trim() 
            
            storeTran(enPos, enLine, frame.tranTextArea.text)
            
            enPos++
            ruPos++
            new File(stateFileName).text = JsonOutput.toJson(["en":enPos, "ru":ruPos])
            updateScreenText(true)
        })
    }

    void configureButton(b1, key, bracket, func) {
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                func()
            }
        };
        b1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), "func");
        b1.getActionMap().put("func", action); 
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                func()
            }
        });
        b1.setMultiClickThreshhold(500)
        b1.text = highlightKey(b1.text, key, bracket)
    }

    String wordbreak(s) {
        int maxlen = 110
        def ww = s.split('\\s+')
        StringBuilder sb = new StringBuilder()
        int k = 0
        for (String w in ww) {
            sb.append(w)
            k += w.length()
            if (k>maxlen) {
                sb.append('\n')
                k = 0
            } else {
                sb.append(' ')
                k++
            }
        }
        return sb.toString().trim()
    }

}

