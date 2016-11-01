# CSV parse project

# Task
This repository contains solution for the given problem:

On the input you've got `3` files containing:
 
* `data.csv` - product representation with `price`, `currency`, `quantity`, `matching_id`
* `currencies.csv` - currency code and ratio to `PLN`, ie. `GBP,2.4` can be
converted to `PLN` with procedure `1 PLN * 2.4`
* `matchings.csv` - matching data `matching_id`, `top_priced_count`

Now, read all the data. From products with particular `matching_id` take those
with the highest total price `(price * quantity)`, limit data set by
`top_priced_count` and aggregate prices.
Result save to `top_products.csv` with five columns:
`matching_id,total_price,avg_price,currency,ignored_products_count`.

# Notes
As I didn't have more details for the implementation itself I have decided that
guys giving me task wanted to see my code so I have used pure Java as much as possible.

In real world I would rather implement whole (or biggest part of) logic inside some DB
engine.

Next point - parsing of the csv document. I have also implemented my own bicycle and
in the real world I would rather use library [jackson-dataformat-csv][jackson-dataformat-csv]
or delegate the task to DB engine.

# Implementation decisions
Not sure that I've got the result wanted from me but I've implemented the logic for
average value calculation for the product currency.

That said if we have products with different currencies but the same matching id than
average will be calculated and written to each row in its own currency.

[jackson-dataformat-csv]: https://github.com/FasterXML/jackson-dataformat-csv