package com.pratham.mis.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class Csv {

    public static synchronized <T> List<T> readAll(String path, Function<CSVRecord, T> mapper) {
        try (Reader in = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
            CSVFormat fmt = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
            Iterable<CSVRecord> records = fmt.parse(in);
            List<T> list = new ArrayList<>();
            for (CSVRecord r : records) list.add(mapper.apply(r));
            return list;
        } catch (IOException e) {
            throw new RuntimeException("CSV read failed: " + path, e);
        }
    }

    public static synchronized void writeAll(String path, String[] headers, List<String[]> rows) {
        try (Writer out = Files.newBufferedWriter(Path.of(path), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            CSVPrinter printer = CSVFormat.DEFAULT.print(out);
            printer.printRecord((Object[]) headers);
            for (String[] row : rows) {
                printer.printRecord((Object[]) row);
            }
            printer.flush();
        } catch (IOException e) {
            throw new RuntimeException("CSV write failed: " + path, e);
        }
    }

    public static synchronized void append(String path, String[] row) {
        try (Writer out = Files.newBufferedWriter(Path.of(path), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
             CSVPrinter printer = CSVFormat.DEFAULT.print(out)) {
            printer.printRecord((Object[]) row);
            printer.flush();
        } catch (IOException e) {
            throw new RuntimeException("CSV append failed: " + path, e);
        }
    }

    public static long nextId(List<? extends HasId> items) {
        return items.stream().mapToLong(HasId::getId).max().orElse(0L) + 1L;
    }

    public interface HasId { long getId(); }
}

