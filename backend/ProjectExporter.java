import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class ProjectExporter {

    // Konfiguration
    private static final String OUTPUT_FILE = "backend_dump.txt";
    private static final List<String> IGNORED_DIRS = List.of("target", ".git", ".idea", ".mvn", "wrapper");
    private static final List<String> INCLUDED_EXTENSIONS = List.of(".java", ".xml", ".yml", ".properties");

    public static void main(String[] args) {
        Path startPath = Paths.get(".");
        Path outputPath = Paths.get(OUTPUT_FILE);

        try (var writer = Files.newBufferedWriter(outputPath)) {
            Files.walkFileTree(startPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (IGNORED_DIRS.contains(dir.getFileName().toString())) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileName = file.toString();
                    if (INCLUDED_EXTENSIONS.stream().anyMatch(fileName::endsWith) && !fileName.equals("ProjectExporter.java")) {
                        writeFileContent(file, writer);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("Export erfolgreich! Datei erstellt: " + OUTPUT_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeFileContent(Path file, java.io.BufferedWriter writer) throws IOException {
        String separator = "---";
        // Normalisiere Pfad für Windows/Unix Konsistenz
        String pathString = file.toString().replace("\\", "/");

        writer.write("\n" + separator + " START FILE: " + pathString + " " + separator + "\n");
        writer.write(Files.readString(file));
        writer.write("\n" + separator + " END FILE: " + pathString + " " + separator + "\n");
    }
}