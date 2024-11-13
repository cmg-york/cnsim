

## Table of Units

### *(Work in progress)*

Please note that standard deviations are always denominated in the same
units as the underlying measure, and must always be positive values[1].

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th>Value</th>
<th>Units</th>
<th>References</th>
<th>Domain Restrictions</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Arrival Rates of Transactions</td>
<td>Transactions/second</td>
<td>txArrivalIntervalRate</td>
<td>Non-negative real numbers (4-byte Floating Point Numbers)</td>
</tr>
<tr class="even">
<td>Transaction Sizes</td>
<td>Bytes</td>
<td>txSizeMean, txSizeSD</td>
<td><span class="math inline">$\R_{\ge 0}$</span> (4-byte FP)</td>
</tr>
<tr class="odd">
<td>Transaction Values</td>
<td>Tokens (of the given cryptocurrency)</td>
<td>txValueMean, txValueSD</td>
<td><span class="math inline">$\R_{\ge 0}$</span> (4-byte FP)</td>
</tr>
<tr class="even">
<td>Hash power</td>
<td>Hashes/second</td>
<td>nodeHashPowerMean, nodeHashPowerSD</td>
<td><span class="math inline">$\R_{\ge 0}$</span> (4-byte FP)</td>
</tr>
<tr class="odd">
<td>Power consumption</td>
<td>Watts (joules/second)</td>
<td>nodeElectricPowerMean, nodeElectricPowerSD</td>
<td><span class="math inline">$\R_{\ge 0}$</span> (4-byte FP)</td>
</tr>
<tr class="even">
<td>Electricity Costs</td>
<td>$<a href="#fn1" class="footnote-ref" id="fnref1"
role="doc-noteref"><sup>1</sup></a>/kWh<a href="#fn2"
class="footnote-ref" id="fnref2"
role="doc-noteref"><sup>2</sup></a></td>
<td>nodeElectricCostMean, nodeElectricCostSD</td>
<td><span class="math inline">$\R_{\ge 0}$</span> (4-byte FP)</td>
</tr>
<tr class="odd">
<td>Throughput</td>
<td>Bps (bits/second)</td>
<td>netThroughputMean, netThroughputSD</td>
<td><span class="math inline">$\R_{\ge 0}$</span> (4-byte FP)</td>
</tr>
<tr class="even">
<td>Difficulty</td>
<td>A ratio<a href="#fn3" class="footnote-ref" id="fnref3"
role="doc-noteref"><sup>3</sup></a></td>
<td>difficulty</td>
<td><span class="math inline">$\R_{\ge 0}$</span> (8-byte FP)</td>
</tr>
<tr class="odd">
<td>System times</td>
<td>milliseconds</td>
<td>simTime, sysTime<a href="#fn4" class="footnote-ref" id="fnref4"
role="doc-noteref"><sup>4</sup></a></td>
<td>Positive integers</td>
</tr>
</tbody>
</table>
<section id="footnotes" class="footnotes footnotes-end-of-document"
role="doc-endnotes">
<hr />
<ol>
<li id="fn1"><p>This is a stand-in for any arbitrary fiat
(i.e. government backed) currency. E.g. USD, CAD, yen, etc.<a
href="#fnref1" class="footnote-back" role="doc-backlink">↩︎</a></p></li>
<li id="fn2"><p>Kilowatt-hours are a non-SI unit, equivalent to the
energy of 1000 watts over one hour, i.e. 3,600,000 joules.<a
href="#fnref2" class="footnote-back" role="doc-backlink">↩︎</a></p></li>
<li id="fn3"><p>The difficulty is expressed as the ratio of the search
space over the success space, and can be considered as the
<em>inverse</em> of the probability of a single hash succeeding. e.g. If
a hash has a 0.5% chance of success, then the difficulty would be 200
(out of 200 hashes, 1 is expected to succeed).<a href="#fnref3"
class="footnote-back" role="doc-backlink">↩︎</a></p></li>
<li id="fn4"><p>While both simTime and sysTime are denominated in
milliseconds, simTime is measured as milliseconds since the start of the
simulation and tracks the simulated passage of time in the simulation,
whereas sysTime is measured as milliseconds since 00:00:00 UTC Jan 1,
1970 (i.e. <a href="https://en.wikipedia.org/wiki/Unix_time">unix
time</a>) and tracks the actual passage of time in the real world.<a
href="#fnref4" class="footnote-back" role="doc-backlink">↩︎</a></p></li>
</ol>
</section>

[1] The standard deviation is the square root of the variance, which is
in turn calculated as the probability-weighted sum of the squared
differences between each datum and the expected value (for discrete
distributions). Since probabilities are non-negative and the square of
any real number is non-negative, the square root of a non-negative
number must in turn be positive.  
$\sigma = \sqrt{\sum\_{i=1}^{n} p_i{(x_i - \mu)}^2}$
