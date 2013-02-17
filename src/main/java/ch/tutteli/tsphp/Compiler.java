/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp;

import ch.tutteli.tsphp.exceptions.CompilerException;
import ch.tutteli.tsphp.common.IParser;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITranslator;
import ch.tutteli.tsphp.common.ITranslatorFactory;
import ch.tutteli.tsphp.common.ITypeChecker;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.antlr.runtime.tree.CommonTreeNodeStream;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class Compiler implements ICompiler
{

    private ITypeChecker typeChecker;
    private IParserFactory parserFactory;
    private ExecutorService executorService;
    private int numberOfWorkers;
    //
    private Collection<ICompilerListener> compilerListeners = new ArrayDeque<>();
    private Collection<ITranslatorFactory> translatorFactories;
    //
    private final Collection<CompilationUnitWithStreamDto> compilationUnits = new ArrayDeque<>();
    private final List<Exception> exceptions = new ArrayList<>();
    private boolean isCompiling = false;
    private final Object lock = new Object();
    private Map<String, String> translations = new HashMap<>();

    public Compiler(ITypeChecker aTypeChecker, IParserFactory aParserFactory,
            Collection<ITranslatorFactory> theTranslatorFactories, int aNumberOfWorkers) {

        typeChecker = aTypeChecker;
        parserFactory = aParserFactory;
        numberOfWorkers = aNumberOfWorkers;
        translatorFactories = theTranslatorFactories;
        executorService = Executors.newFixedThreadPool(numberOfWorkers);
    }

    @Override
    public void registerCompilerListener(ICompilerListener listener) {
        compilerListeners.add(listener);
    }

    @Override
    public boolean hasFoundError() {
        return !exceptions.isEmpty();
    }

    @Override
    public List<Exception> getExceptions() {
        return exceptions;
    }

    @Override
    public void addCompilationUnit(String id, final String string) {
        add(new ParserAndDefinitionPhaseRunner(id, new IParserMethod()
        {
            @Override
            public ITSPHPAst parser(IParser parser) {
                return parser.parse(string);
            }
        }));
    }

    private void add(ParserAndDefinitionPhaseRunner runner) {
        boolean notYetCompiling;
        synchronized (lock) {
            notYetCompiling = !isCompiling;
        }
        if (notYetCompiling) {
            executorService.execute(runner);
        } else {
            exceptions.add(new CompilerException("Tried to parse after calling compile()"));
        }
    }

    @Override
    public void addCompilationUnit(String id, final char[] chars, final int numberOfActualCharsInArray) {
        add(new ParserAndDefinitionPhaseRunner(id, new IParserMethod()
        {
            @Override
            public ITSPHPAst parser(IParser parser) {
                return parser.parse(chars, numberOfActualCharsInArray);
            }
        }));
    }

    @Override
    public void addCompilationUnit(String id, final InputStream inputStream) throws IOException {
        add(new ParserAndDefinitionPhaseRunner(id, new IParserMethod()
        {
            @Override
            public ITSPHPAst parser(IParser parser) throws IOException {
                return parser.parseInputStream(inputStream);
            }
        }));
    }

    @Override
    public void addCompilationUnit(String id, final InputStream inputStream, final int size) throws IOException {
        add(new ParserAndDefinitionPhaseRunner(id, new IParserMethod()
        {
            @Override
            public ITSPHPAst parser(IParser parser) throws IOException {
                return parser.parseInputStream(inputStream, size);
            }
        }));
    }

    @Override
    public void addCompilationUnit(String id, final InputStream inputStream, final String encoding) throws IOException {
        add(new ParserAndDefinitionPhaseRunner(id, new IParserMethod()
        {
            @Override
            public ITSPHPAst parser(IParser parser) throws IOException {
                return parser.parseInputStream(inputStream, encoding);
            }
        }));
    }

    @Override
    public void addCompilationUnit(String id, final InputStream inputStream, final int size, final String encoding)
            throws IOException {
        add(new ParserAndDefinitionPhaseRunner(id, new IParserMethod()
        {
            @Override
            public ITSPHPAst parser(IParser parser) throws IOException {
                return parser.parseInputStream(inputStream, size, encoding);
            }
        }));
    }

    @Override
    public void addCompilationUnit(String id, final InputStream inputStream, final int size, final int readBufferSize,
            final String encoding) throws IOException {
        add(new ParserAndDefinitionPhaseRunner(id, new IParserMethod()
        {
            @Override
            public ITSPHPAst parser(IParser parser) throws IOException {
                return parser.parseInputStream(inputStream, size, readBufferSize, encoding);
            }
        }));
    }

    @Override
    public void addFile(final String pathToFileInclFileName) throws IOException {

        add(new ParserAndDefinitionPhaseRunner(pathToFileInclFileName, new IParserMethod()
        {
            @Override
            public ITSPHPAst parser(IParser parser) throws IOException {
                return parser.parseFile(pathToFileInclFileName);
            }
        }));
    }

    @Override
    public void addFile(final String pathToFileInclFileName, final String encoding) throws IOException {
        add(new ParserAndDefinitionPhaseRunner(pathToFileInclFileName, new IParserMethod()
        {
            @Override
            public ITSPHPAst parser(IParser parser) throws IOException {
                return parser.parseFile(pathToFileInclFileName, encoding);
            }
        }));
    }

    @Override
    public void compile() {
        boolean isNotAlreadyCompiling;
        synchronized (lock) {
            isNotAlreadyCompiling = !isCompiling;
            isCompiling = true;
        }
        if (isNotAlreadyCompiling) {
            waitUntilExecutorFinished(new Runnable()
            {
                @Override
                public void run() {
                    doReferencePhase();
                }
            });
        }
    }

    private void waitUntilExecutorFinished(Runnable callback) {
        for (int i = 0; i < numberOfWorkers; ++i) {
            executorService.execute(new Runnable()
            {
                @Override
                public void run() {
                    //do nothing just make sure added tasks before are done
                }
            });
        }
        executorService.execute(callback);
    }

    private void doReferencePhase() {
        if (!compilationUnits.isEmpty()) {
            for (CompilationUnitWithStreamDto compilationUnit : compilationUnits) {
                executorService.execute(new ReferencePhaseRunner(compilationUnit));
            }
            waitUntilExecutorFinished(new Runnable()
            {
                @Override
                public void run() {
                    doTranslation();
                }
            });
        }
    }

    private void doTranslation() {
        if (translatorFactories != null) {
            for (final ITranslatorFactory translatorFactory : translatorFactories) {
                for (final CompilationUnitWithStreamDto compilationUnit : compilationUnits) {
                    executorService.execute(new Runnable()
                    {
                        @Override
                        public void run() {
                            try {
                                translations.put(compilationUnit.id,
                                        translatorFactory.build().translate(compilationUnit.compilationUnit));
                            } catch (IOException ex) {
                                exceptions.add(ex);
                            }
                        }
                    });
                }
            }
            waitUntilExecutorFinished(new Runnable()
            {
                @Override
                public void run() {
                    updateListener();
                }
            });
        }
    }

    @Override
    public Map<String, String> getTranslations() {
        return translations;
    }

    private void updateListener() {
        for (ICompilerListener listener : compilerListeners) {
            listener.afterCompilingCompleted();
        }
    }

    private interface IParserMethod
    {

        public ITSPHPAst parser(IParser parser) throws IOException;
    }

    private class ParserAndDefinitionPhaseRunner implements Runnable
    {

        private IParserMethod parserMethod;
        private String id;

        public ParserAndDefinitionPhaseRunner(String theId, IParserMethod aParserMethod) {
            parserMethod = aParserMethod;
            id = theId;
        }

        @Override
        public void run() {
            try {
                IParser parser = parserFactory.build();
                ITSPHPAst ast = parserMethod.parser(parser);
                exceptions.addAll(parser.getExceptions());

                CommonTreeNodeStream commonTreeNodeStream = new CommonTreeNodeStream(
                        parserFactory.getTSPHPAstAdaptor(), ast);
                commonTreeNodeStream.setTokenStream(parser.getTokenStream());

                typeChecker.enrichWithDefinitions(ast, commonTreeNodeStream);
                compilationUnits.add(new CompilationUnitWithStreamDto(id, ast, commonTreeNodeStream));

            } catch (IOException ex) {
                exceptions.add(ex);
            }
        }
    }

    private class ReferencePhaseRunner implements Runnable
    {

        CompilationUnitWithStreamDto dto;

        ReferencePhaseRunner(CompilationUnitWithStreamDto aDto) {
            dto = aDto;
        }

        @Override
        public void run() {
            dto.treeNodeStream.reset();
            typeChecker.enrichWithReferences(dto.compilationUnit, dto.treeNodeStream);
        }
    }

    private class CompilationUnitWithStreamDto
    {

        public String id;
        public ITSPHPAst compilationUnit;
        public CommonTreeNodeStream treeNodeStream;

        public CompilationUnitWithStreamDto(String theId, ITSPHPAst theCompilationUnit,
                CommonTreeNodeStream theTreeNodeStream) {
            id = theId;
            compilationUnit = theCompilationUnit;
            treeNodeStream = theTreeNodeStream;
        }
    }
}
