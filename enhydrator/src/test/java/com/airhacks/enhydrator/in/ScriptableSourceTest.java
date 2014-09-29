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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import javax.script.ScriptException;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author airhacks.com
 */
public class ScriptableSourceTest {

    ScriptableSource cut;

    @Before
    public void init() throws FileNotFoundException {
        final Path pathToContent = Paths.get("./src/test/files/languages.json");
        Reader script = new FileReader("./src/test/files/converter.js");
        this.cut = new ScriptableSource(pathToContent, script, "UTF-8");
        this.cut.init();

    }

    @Test
    public void loadArray() throws ScriptException, IOException {
        Object result = this.cut.load();
        System.out.println("result = " + result);
        assertNotNull(result);
    }

    @Test
    public void query() {
        Iterable<Row> query = this.cut.query(null, null);
        assertNotNull(query);
        Row theOnly = query.iterator().next();
        assertNotNull(theOnly);
        Collection<Column> columns = theOnly.getColumns();
        assertFalse(columns.isEmpty());
    }

}
