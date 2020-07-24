# Queries without optimize

## Q1

PREFIX ex: <http://example.org/>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

%ProductType% = <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType758>
%ProductFeature1% = "conformists"

#### Parte 1

SELECT *
WHERE {
 ?product rdfs:label ?label .
 ?product rdf:type %ProductType% .
}

Contar ?label

#### Parte 2

SELECT *
WHERE {
 ?product rdfs:label ?label .
 ?product rdf:type %ProductType% .
 ?product bsbm:productFeature %ProductFeature1%
}

Contar ?label

## Q2

PREFIX ex: <http://example.org/>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX pr6: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/>

%ProductXYZ% = pr6:Product250

#### Parte 1

SELECT *
WHERE {
  %ProductXYZ% rdfs:label ?label .
  %ProductXYZ% rdfs:comment ?comment .
  %ProductXYZ% bsbm:producer ?p .
  ?p rdfs:label ?producer .
}

Contar ?label

#### Parte 2

SELECT *
WHERE {
  %ProductXYZ% rdfs:label ?label .
  %ProductXYZ% rdfs:comment ?comment .
  %ProductXYZ% bsbm:producer ?p .
  ?p rdfs:label ?producer .
  %ProductXYZ% bsbm:productFeature ?f .
}

Contar ?label

#### Parte 3

SELECT *
WHERE {
  %ProductXYZ% rdfs:label ?label .
  %ProductXYZ% rdfs:comment ?comment .
  %ProductXYZ% bsbm:producer ?p .
  ?p rdfs:label ?producer .
  %ProductXYZ% dc:publisher ?p .
  %ProductXYZ% bsbm:productFeature ?f .
  ?f rdfs:label ?productFeature .
  %ProductXYZ% bsbm:productPropertyTextual1 ?propertyTextual1 .
  %ProductXYZ% bsbm:productPropertyTextual2 ?propertyTextual2 .
  %ProductXYZ% bsbm:productPropertyTextual3 ?propertyTextual3 .
}

Contar ?label

## Q3

PREFIX ex: <http://example.org/>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

%ProductType% = <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType758>

SELECT *
WHERE {
  ?product rdfs:label ?label .
  ?product rdf:type %ProductType% .
}

Contar ?label

## Q4

PREFIX ex: <http://example.org/>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

%ProductType% = <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType758>
%ProductFeature1% = "conformists"

#### Parte 1

SELECT *
WHERE {
  ?product rdfs:label ?label .
  ?product rdf:type %ProductType% .
}

Contar ?label

#### Parte 2

SELECT ?product ?label
WHERE {
 ?product rdfs:label ?label .
 ?product rdf:type %ProductType% .
 ?product bsbm:productFeature %ProductFeature1% .
}

Contar ?label

## Q5

PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/>
PREFIX ex: <http://example.org/>
PREFIX rev: <http://purl.org/stuff/rev#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX pr: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer2233/>

%ProductXYZ% = pr:Product112553
%ProductType% = <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType758>

#### Parte 1

SELECT *
WHERE {
  ?product rdfs:label ?label .
  ?product rdf:type %ProductType% .
  %ProductXYZ% rdfs:label ?label2 .
  FILTER (%ProductXYZ% != ?product)
  %ProductXYZ% bsbm:productFeature ?f .
  ?product bsbm:productFeature ?f
}

Contar ?label

#### Parte 2

SELECT *
WHERE {
  ?product rdfs:label ?label .
  ?product rdf:type %ProductType% .
  %ProductXYZ% rdfs:label ?label2 .
  FILTER (%ProductXYZ% != ?product)
  %ProductXYZ% bsbm:productFeature ?f .
  ?product bsbm:productFeature ?f .
  ?product bsbm:productPropertyNumeric1 ?simProperty1 .
}

Contar ?label2

#### Parte 3

SELECT *
WHERE {
  ?product rdfs:label ?label .
  ?product rdf:type %ProductType% .
  %ProductXYZ% rdfs:label ?label2 .
  FILTER (%ProductXYZ% != ?product)
  %ProductXYZ% bsbm:productFeature ?f .
  ?product bsbm:productFeature ?f .
  ?product bsbm:productPropertyNumeric1 ?simProperty1 .
  %ProductXYZ% bsbm:productPropertyNumeric1 ?origProperty1 .
  FILTER (?simProperty1 < (?origProperty1 + 120) && ?simProperty1 >
 (?origProperty1 - 120))
}

Contar ?label

#### Parte 4

SELECT *
WHERE {
  ?product rdfs:label ?label .
  ?product rdf:type %ProductType% .
  %ProductXYZ% rdfs:label ?label2 .
  FILTER (%ProductXYZ% != ?product)
  %ProductXYZ% bsbm:productFeature ?f .
  ?product bsbm:productFeature ?f .
  ?product bsbm:productPropertyNumeric1 ?simProperty1 .
  %ProductXYZ% bsbm:productPropertyNumeric1 ?origProperty1 .
  FILTER (?simProperty1 < (?origProperty1 + 120) && ?simProperty1 >
 (?origProperty1 - 120)) .
  ?product bsbm:productPropertyNumeric2 ?simProperty2 .

}

Contar ?label2

## Q7

PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/>
PREFIX ex: <http://example.org/>
PREFIX rev: <http://purl.org/stuff/rev#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX pr6: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/>

#### Parte 1

%ProductXYZ% = pr6:Product250

SELECT * WHERE {
  %ProductXYZ% rdfs:label ?label .
  ?offer bsbm:product %ProductXYZ% .
}

Contar ?offer

#### Parte 2

SELECT * WHERE {
  %ProductXYZ% rdfs:label ?label .
  ?offer bsbm:product %ProductXYZ% .
  ?offer bsbm:price ?price .
  ?offer bsbm:vendor ?vendor .
  ?vendor bsbm:country <http://downlode.org/rdf/iso-3166/countries#DE> .
  ?review bsbm:reviewFor %ProductXYZ% .
}

Contar ?review


## Q8


PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> PREFIX ex: <http://example.org/>
PREFIX rev: <http://purl.org/stuff/rev#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX pr5: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer5/>

%ProductXYZ% = pr5:Product201

#### Parte 1

SELECT * WHERE {
  %ProductXYZ% rdfs:label ?label .
  ?review bsbm:reviewFor %ProductXYZ% .
}

Contar ?review

## Q10

PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/>
PREFIX ex: <http://example.org/>
PREFIX rev: <http://purl.org/stuff/rev#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX pr6: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/>

%ProductXYZ% = pr6:Product250

#### Parte 1

SELECT * WHERE {
  %ProductXYZ% rdfs:label ?label .
  ?offer bsbm:product %ProductXYZ% .
}

Contar ?offer

## Q12

PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/>
PREFIX ex: <http://example.org/>
PREFIX rev: <http://purl.org/stuff/rev#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX ve5: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromVendor5/>

%OfferXYZ% = ve5:Offer9220

SELECT * WHERE {
  %OfferXYZ% bsbm:product ?p .
  ?p rdfs:label ?label .
}

Contar ?p
