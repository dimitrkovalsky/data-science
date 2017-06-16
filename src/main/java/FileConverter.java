import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dkovalskyi
 * @since 26.05.2017
 */
public class FileConverter {
    public static void main(String[] args) throws IOException {
        cleanSheva();

    }

    private static void cleanRus() throws IOException {
        List<String> lines = FileUtils.readLines(new File("C:\\github\\data-science\\text\\rus.txt"), StandardCharsets.UTF_8);

        int beforeClean = lines.size();
        lines = lines.stream().filter(l -> !l.contains("Автор текста") &&
            !l.contains("Кб)") &&
            !l.contains("Смотреть видео") &&
            !l.contains("mp3") &&
            !l.contains("текст (слова) и музыка")).collect(Collectors.toList());
        lines = lines.stream().filter(l -> !StringUtils.isEmpty(l.trim())).collect(Collectors.toList());
        lines = lines.stream().map(String::trim).collect(Collectors.toList());

        int afterClean = lines.size();
        FileUtils.writeLines(new File("C:\\github\\data-science\\text\\rus-clean.txt"), lines);
        System.out.println("Before " + beforeClean + " after " + afterClean);
    }

    private static void cleanMarks() throws IOException {
        List<String> lines = FileUtils.readLines(new File("C:\\github\\data-science\\text\\kapital.txt"), StandardCharsets.UTF_8);

        int beforeClean = lines.size();
        lines = lines.stream().filter(l -> !StringUtils.isEmpty(l.trim())).collect(Collectors.toList());
        int afterClean = lines.size();
        FileUtils.writeLines(new File("C:\\github\\data-science\\text\\kapital-clean.txt"), lines);
        System.out.println("Before " + beforeClean + " after " + afterClean);
    }

    private static void cleanSheva() throws IOException {
        List<String> lines = FileUtils.readLines(new File("E:\\github\\data-science\\text\\kobzar.txt"), StandardCharsets.UTF_8);

        int beforeClean = lines.size();
        lines = lines.stream()
                .filter(l -> !StringUtils.isEmpty(l.trim()))
                .filter(l -> !l.startsWith("*"))
                .filter(l -> !l.startsWith("."))
                .filter(l -> !l.startsWith("["))
                .filter(l -> !l.endsWith("]"))
                .filter(l -> !l.endsWith("___"))
                .filter(l -> !l.endsWith("---"))
                .filter(l -> !l.contains("18"))
                .collect(Collectors.toList());
        int afterClean = lines.size();
        FileUtils.writeLines(new File("E:\\github\\data-science\\text\\kobzar-clean.txt"), lines);
        System.out.println("Before " + beforeClean + " after " + afterClean);
    }
}
