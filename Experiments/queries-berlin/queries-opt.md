# Queries optimized

## Q1

PREFIX ex: <http://example.org/>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

%ProductType% = <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType758>
%ProductFeature1% = "conformists"

#### Part 1

SELECT *
WHERE {
 ?product rdfs:label ?label .
 ?product bsbm:productPropertyNumeric1 ?value1 .
 FILTER (?value1 > %x%)
 ?product rdf:type %ProductType% .
}

Count ?label

#### Part 2

SELECT *
WHERE {
 ?product rdfs:label ?label .
 ?product bsbm:productPropertyNumeric1 ?value1 .
 FILTER (?value1 > %x%)
 ?product rdf:type %ProductType% .
 ?product bsbm:productFeature %ProductFeature1%
}

Count ?label

## Q2

PREFIX ex: <http://example.org/>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX pr6: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/>

%ProductXYZ% = pr6:Product250

#### Part 1

SELECT *
WHERE {
  %ProductXYZ% rdfs:label ?label .
  %ProductXYZ% rdfs:comment ?comment .
  %ProductXYZ% bsbm:producer ?p .
  ?p rdfs:label ?producer .
  %ProductXYZ% bsbm:productPropertyTextual4 ?p4 .
  %ProductXYZ% bsbm:productPropertyTextual5 ?p5
}

Count ?label

#### Part 2

SELECT *
WHERE {
  %ProductXYZ% rdfs:label ?label .
  %ProductXYZ% rdfs:comment ?comment .
  %ProductXYZ% bsbm:producer ?p .
  ?p rdfs:label ?producer .
  %ProductXYZ% bsbm:productPropertyTextual4 ?p4 .
  %ProductXYZ% bsbm:productPropertyTextual5 ?p5
  %ProductXYZ% bsbm:productFeature ?f .
}

Count ?label

#### Part 3

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
  %ProductXYZ% bsbm:productPropertyTextual4 ?p4 .
  %ProductXYZ% bsbm:productPropertyTextual5 ?p5
}

Count ?label

## Q3

PREFIX ex: <http://example.org/>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

%ProductType% = <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType758>

SELECT *
WHERE {
  ?product rdfs:label ?label .
  ?product rdf:type %ProductType% .
  ?product bsbm:productPropertyNumeric1 ?p1 . FILTER ( ?p1 > 100)
  ?product bsbm:productPropertyNumeric2 ?p2 FILTER (?p2 < 500 )
}

Count ?label

## Q4

PREFIX ex: <http://example.org/>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

%ProductType% = <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType758>
%ProductFeature1% = "conformists"

#### Part 1

SELECT *
WHERE {
  ?product rdfs:label ?label .
  ?product rdf:type %ProductType% .
  ?product bsbm:productPropertyNumeric1 ?p1 . FILTER (?p1 > 500)
}

Count ?label

#### Part 2

SELECT ?product ?label
WHERE {
 ?product rdfs:label ?label .
 ?product rdf:type %ProductType% .
 ?product bsbm:productFeature %ProductFeature1% .
 ?product bsbm:productPropertyNumeric1 ?p1 . FILTER (?p1 > 500)
}

Count ?label

## Q5

PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/>
PREFIX ex: <http://example.org/>
PREFIX rev: <http://purl.org/stuff/rev#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX pr: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer2233/>

%ProductXYZ% = pr7:Product112553
%ProductType% = <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType758>

#### Part 1

SELECT *
WHERE {
  ?product rdfs:label ?label .
  ?product rdf:type %ProductType% .
  %ProductXYZ% rdfs:label ?label2 .
  FILTER (%ProductXYZ% != ?product)
  %ProductXYZ% bsbm:productFeature ?f .
  ?product bsbm:productFeature ?f
}

Count ?label

#### Part 2

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

Count ?label2

#### Part 3

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

Count ?label

#### Part 4

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

Count ?label2

## Q7

PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/>
PREFIX ex: <http://example.org/>
PREFIX rev: <http://purl.org/stuff/rev#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX pr6: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/>


#### Part 1

%ProductXYZ% = pr6:Product250

SELECT * WHERE {
  %ProductXYZ% rdfs:label ?label .
  ?offer bsbm:product %ProductXYZ% .
}

Count ?offer

#### Part 2 (Da 0)

SELECT * WHERE {
  %ProductXYZ% rdfs:label ?label .
  ?offer bsbm:product %ProductXYZ% .
  ?offer bsbm:price ?price .
  ?offer bsbm:vendor ?vendor .
  ?vendor bsbm:country <http://downlode.org/rdf/iso-3166/countries#DE> .
  ?review bsbm:reviewFor %ProductXYZ% .
  ?review bsbm:rating1 ?rating1 .
  ?review bsbm:rating2 ?rating2
}

Count ?review


## Q8


PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> PREFIX ex: <http://example.org/>
PREFIX rev: <http://purl.org/stuff/rev#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX pr5: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer5/>

%ProductXYZ% = pr5:Product201

#### Part 1

SELECT * WHERE {
  %ProductXYZ% rdfs:label ?label .
  ?review bsbm:reviewFor %ProductXYZ% .
  ?review bsbm:rating1 ?rating1 .
  ?review bsbm:rating2 ?rating2 .
  ?review bsbm:rating3 ?rating3 .
  ?review bsbm:rating4 ?rating4
}

Count ?review

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

#### Part 1

SELECT * WHERE {
  %ProductXYZ% rdfs:label ?label .
  ?offer bsbm:product %ProductXYZ% .
  ?offer bsbm:deliveryDays ?devDays .
    FILTER(?devDays < 3)
}

Count ?offer

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
  %OfferXYZ% bsbm:deliveryDays ?devDays .
  %OfferXYZ% bsbm:offerWebpage ?offerURL .
  %OfferXYZ% bsbm:validTo ?validTo
}

Count ?p
