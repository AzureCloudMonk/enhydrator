/*
 * Copyright 2014 Adam Bien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.airhacks.enhydrator.in;

/*
 * #%L
 * enhydrator
 * %%
 * Copyright (C) 2014 Adam Bien
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author airhacks.com
 */
public class CSVSource implements Source {

    private String delimiter;
    private Path file;
    private Stream<String> lines;
    private final boolean fileContainsHeaders;

    private int counter;

    private List<String> columnNames;
    private List<Row> iterable;

    public CSVSource(String file, String delimiter, boolean fileContainsHeaders) {
        this.file = Paths.get(file);
        if (!Files.exists(this.file)) {
            throw new IllegalArgumentException(file + " does not exist !");
        }
        this.delimiter = delimiter;
        this.fileContainsHeaders = fileContainsHeaders;
        this.columnNames = new ArrayList<>();
        try {
            this.lines = Files.lines(this.file);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot parse lines");
        }
        if (fileContainsHeaders) {
            this.columnNames = this.extractHeaders();
        }
    }

    final List<String> extractHeaders() {
        String headerLine = null;
        try {
            headerLine = Files.lines(this.file).findFirst().get();
        } catch (IOException ex) {
            Logger.getLogger(CSVSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        Row headers = parse(headerLine, this.delimiter);

        return headers.getColumns().values().stream().
                map(e -> ((String) e)).
                collect(Collectors.toList());
    }

    /**
     *
     * @param query not supported yet
     * @param params not supported yet
     * @return
     */
    @Override
    public Iterable<Row> query(String query, Object... params) {
        if (this.iterable == null) {
            this.iterable = this.lines.map(s -> parse(s, this.delimiter)).collect(Collectors.toList());
        }
        return this.iterable;
    }

    Row parse(String line, String delimiter) {
        String[] splitted = line.split(delimiter);
        if (splitted == null || splitted.length == 0) {
            return null;
        }
        Row row = new Row(counter++);
        for (int i = 0; i < splitted.length; i++) {
            String value = splitted[i];
            String columnName = getColumnName(i);
            row.addColumn(columnName, value);
        }
        return row;
    }

    String getColumnName(int slot) {
        if (slot < this.columnNames.size()) {
            return this.columnNames.get(slot);
        } else {
            String name = String.valueOf(slot);
            if (slot >= this.columnNames.size()) {
                this.columnNames.add(name);
            } else {
                this.columnNames.set(slot, name);
            }
            return name;
        }
    }

}
