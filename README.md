# en-tran

```
                       __
     ____   ____     _/  |_____________    ____
   _/ __ \ /    \    \   __\_  __ \__  \  /    \
   \  ___/|   |  \    |  |  |  | \// __ \|   |  \
    \___  >___|  /____|__|  |__|  (____  /___|  /
        \/     \/_____/                \/     \/
Usage: en_tran [-hvV] [-e=<enFile>] [-r=<ruFile>] <databaseName>
Finding matches between the English original and the Russian translation.
      <databaseName>       Database name.
  -e, --en-file=<enFile>   HTML file with English text
  -h, --help               Show this help message and exit.
  -r, --ru-file=<ruFile>   HTML file with Russian text
  -v, --verbose            Verbose mode
  -V, --version            Print version information and exit.
```

This application is designed to assist in reading fiction books in English by finding matches between English text and its translation in another language, such as Russian. It requires two HTML files: one with the English text and one with the translated text.

### Preparing Your Files

1. **Convert EPUB to HTMLZ**: Begin by converting your EPUB books into HTMLZ format. HTMLZ is a ZIP file that contains an `index.html` file, which is suitable for our purposes. You can use [Calibre](https://calibre-ebook.com/) to do this.

2. **Unpack the HTMLZ Files**: Once converted, unpack the HTMLZ files to your desired location. It's recommended to create a new folder for each bilingual project and unpack the HTMLZ contents into `en` (English) and `ru` (Russian) subfolders accordingly.

3. **Prepare Configuration File in YAML Format**: You'll need to create a `.yml` file that specifies the CSS classes used to mark paragraphs in the HTML files. This file should be named after your bilingual project and placed in the same directory as your HTML files.

Here is an example of `.yml` file content:

```yml
paragraphs:
    en:
    - calibre1
    ru:
    - calibre7
    - para
    - paranoindent
    - paragraph
    - chapteropenertext
    - extracttextnoindent

start:
    en: 3
    ru: 14
```

Here, `paragraphs.en` and `paragraphs.ru` list the CSS classes for matching paragraphs. The `start.en` and `start.ru` specify the starting paragraphs to ignore any headers.

Name of `.yml` file should be the same as database name.

### Running the Application


You'll be presented with a split-screen interface:
- The **top section** displays the list of English paragraphs.
- The **bottom section** shows the Russian translations.

#### Navigation Buttons

- **Prev / Next**: Scrolls through English and Russian paragraphs simultaneously without updating the database.
- **Prev RU / Next RU**: Scrolls through only Russian translations without database updates.
- **Add**: Combines the next Russian paragraph with the current one, without updating the database.
- **Add Next**: Saves the current match to the database and moves to the next paragraph.

---

By following these steps, you can efficiently create a bilingual database for your reading projects, facilitating a deeper understanding of the texts in both languages.
