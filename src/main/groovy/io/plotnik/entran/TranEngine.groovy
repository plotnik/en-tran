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
    int dbPos = 1;

    List<String> enPar = [];
    List<String> ruPar = [];

    String enLine;
    String ruLine;

    String stateFileName;
    String enFile;
    String ruFile;

    EnTranFrame frame;

    static boolean verbose = Main.verbose;
    
    TranEngine(String databaseName, String enFile, String ruFile) {

        this.databaseName = databaseName
        this.enFile = enFile
        this.ruFile = ruFile

        // Read CSS classes for paragraphs
        
        String configFile = databaseName + ".yml"
        YamlSlurper yamlSlurper = new YamlSlurper()
        
        def config = yamlSlurper.parseText(new File(configFile).text)
        
        // Parse HTML
        
        XmlParser parser = new XmlParser(new Parser())
        
        def enTree = parser.parseText(new File(enFile).text)
        def ruTree = parser.parseText(new File(ruFile).text)

        enPar = extractParagraphs(enTree, config.paragraphs.en)
        enPar = enPar.drop(config.start.en)
        if (verbose) {
            println "enPar size: ${enPar.size()}"
        }

        ruPar = extractParagraphs(ruTree, config.paragraphs.ru)
        ruPar = ruPar.drop(config.start.ru)
        if (verbose) {
            println "ruPar size: ${ruPar.size()}"
        }
        
        String databaseFile = databaseName + '.tran.db'
        if (!new File(databaseFile).exists()) {
            initDatabase()
        }

        // Open database

        def url = 'jdbc:sqlite:' + databaseName + '.tran.db'
        def user = ''
        def password = ''
        def driver = 'org.sqlite.JDBC'

        sql = Sql.newInstance(url, user, password, driver)
        
        // Set `dbPos` variable to the number of records in Tran table.

        dbPos = sql.firstRow("SELECT COUNT(*) AS count FROM Tran").count

        // Extract paragraphs from db
        // def rows = sql.rows "select * from Tran"
        // for (row in rows) {
        //     enPar << row['en']
        //     ruPar << row['ru']
        // }

        stateFileName = databaseName + ".json"
        readStateFile()
    }
    
    void initDatabase() {
        // Init DB

        sql.execute '''
        drop table if exists Tran;
        '''
        sql.execute '''
        create table Tran (id integer primary key, en string, ru string);
        '''

        // println "Initializing database ${databaseName}..."
        // int maxSize = Math.max(enPar.size(), ruPar.size());
        // for (int i=1; i<=maxSize; i++) {
        //     String en = i>enPar.size()? "": enPar[i-1];
        //     String ru = i>ruPar.size()? "": ruPar[i-1];
        //     sql.execute """
		//     insert into Tran (id, en, ru) values ($i, $en, $ru);
		//     """	
        // }

        enPos = 1;
        ruPos = 1;
        writeStateFile();
    }

    // List of paragraphs
    List<String> extractParagraphs(tree, para) {
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
        ruLine = (enPos <= dbPos)? getRuText() : ruPar[ruPos-1];
        return new Sentence(ruPos, ruLine);
    }

    String getRuText() {
        def res = sql.firstRow "select ru from Tran where id=${enPos}"
        String ruText = res?.ru
        if (verbose) {
            println "----- getRuText: en: ${enPos}: ru: ${ruPos}: ${ruText}\n-----"
        }
        return ruText
    }

    void storeTran(String en, String ru) {
        dbPos++
        sql.execute("insert into Tran (id, en, ru) values ($dbPos, $en, $ru)")

        // sql.execute "replace into Tran (id, en, ru) values ($k, $en, $ru)"
        // if (verbose) {
        //     println "===== storeTran: replace into Tran (id, en, ru) values ($k, $en, $ru)\n====="
        // }
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
            //dbPos = json.db
        } 
    }
    
    void writeStateFile() {
        new File(stateFileName).text = JsonOutput.toJson(["en":enPos, "ru":ruPos]) //, "db":dbPos])
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

    void configureButtons() {

        // Shift text and translation 1 screen back
        configureButton(frame.prevButton, "F7", "<", {
            enPos--
            ruPos--
            frame.updateScreenText(true)
        })

        // Shift text and translation 1 screen forward
        configureButton(frame.nextButton, "F8", ">", {
            enPos++
            ruPos++
            writeStateFile()
            frame.updateScreenText(true)
        })

        // Shift translation 1 screen back without updating db
        configureButton(frame.prevRuButton, "F9", "<", {
            if (ruPos > 1) {
                ruPos--
                frame.updateScreenText(true)
            }
        })
        
        // Shift translation 1 screen forward without updating db
        configureButton(frame.nextRuButton, "F10", ">", {
            if (ruPos < ruPar.size()-1) {
                ruPos++
            }
            frame.updateScreenText(true)
        })


        configureButton(frame.addButton, "F11", "+", {
            ruPos++
            frame.tranTextArea.text = (frame.tranTextArea.text + '\n' + wordbreak(ruLine)).trim() 
            frame.updateScreenText(false)
        })

        // Save ru text and move to the next sentence.
        configureButton(frame.addNextButton, "F12", ">", {
            if (verbose) {    
                println "ruLine: $ruLine"
            }
            frame.tranTextArea.text = frame.tranTextArea.text.trim() 
            
            storeTran(enLine, frame.tranTextArea.text)
            
            enPos++
            ruPos++

            writeStateFile()
            frame.updateScreenText(true)
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

}

