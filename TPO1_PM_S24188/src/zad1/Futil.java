package zad1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

public class Futil {

    public static void processDir(String dirName, String resultFileName) {

        try (FileChannel fout = FileChannel.open(Paths.get(resultFileName), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            Files.walkFileTree(Paths.get(dirName), new SimpleFileVisitor<Path>() {
                final Charset inCharset = Charset.forName("Cp1250");
                final Charset outCharset = StandardCharsets.UTF_8;

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    FileChannel fin = FileChannel.open(file, StandardOpenOption.READ);
                    ByteBuffer buffer = ByteBuffer.allocate((int)fin.size());

                    fin.read(buffer);
                    buffer.flip();

                    CharBuffer charBuffer = inCharset.decode(buffer);

                    buffer = outCharset.encode(charBuffer);
                    fout.write(buffer);

                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
