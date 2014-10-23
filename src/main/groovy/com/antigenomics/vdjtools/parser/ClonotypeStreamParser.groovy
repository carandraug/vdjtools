/**
 Copyright 2014 Mikhail Shugay (mikhail.shugay@gmail.com)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.antigenomics.vdjtools.parser

import com.antigenomics.vdjtools.Clonotype
import com.antigenomics.vdjtools.Software
import com.antigenomics.vdjtools.sample.Sample
import groovy.transform.PackageScope

public abstract class ClonotypeStreamParser implements Iterable<Clonotype> {
    public static ClonotypeStreamParser create(InputStream inputStream, Software software, Sample sample) {
        ClonotypeStreamParser parser
        def reader = new BufferedReader(new InputStreamReader(inputStream))
        def innerIter = reader.iterator()

        switch (software) {
            case Software.MiTcr:
                parser = new MiTcrParser(innerIter, software, sample)
                break
            case Software.IgBlast:
                parser = new IgBlastParser(innerIter, software, sample)
                break
            case Software.Simple:
                parser = new SimpleParser(innerIter, software, sample)
                break
            case Software.MiGec:
                parser = new MiGecParser(innerIter, software, sample)
                break
            default:
                throw new UnsupportedOperationException("Don't know how to parse $software data")
        }

        (1..software.headerLineCount).each { innerIter.next() }

        return parser
    }

    protected final Software software
    protected final Iterator<String> innerIter
    protected final Sample sample

    protected ClonotypeStreamParser(Iterator<String> innerIter, Software software, Sample sample) {
        this.software = software
        this.innerIter = innerIter
        this.sample = sample
    }

    protected abstract Clonotype parse(String clonotypeString)

    @Override
    Iterator<Clonotype> iterator() {
        [hasNext: {
            innerIter.hasNext()
        }, next : {
            parse(innerIter.next())
        }] as Iterator
    }
}