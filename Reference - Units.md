## Table of Units
### _(Work in progress)_

Please note that standard deviations are always denominated in the same units as the underlying measure, and must always be positive values[^1].

| Value | Units | References | Domain Restrictions |
| ------ | ------ | ------ | ------ |
| Arrival Rates of Transactions | Transactions/second | txArrivalIntervalRate | Non-negative real numbers (4-byte Floating Point Numbers) |
| Transaction Sizes | Bytes | txSizeMean, txSizeSD | $\R_{\ge 0}$ (4-byte FP) |
| Transaction Values | Tokens (of the given cryptocurrency) | txValueMean, txValueSD | $\R_{\ge 0}$ (4-byte FP) |
| Hash power | Hashes/second | nodeHashPowerMean, nodeHashPowerSD  | $\R_{\ge 0}$ (4-byte FP) |
| Power consumption | Watts (joules/second) | nodeElectricPowerMean, nodeElectricPowerSD | $\R_{\ge 0}$ (4-byte FP) |
| Electricity Costs | $[^2]/kWh[^3] | nodeElectricCostMean, nodeElectricCostSD | $\R_{\ge 0}$ (4-byte FP) |
| Throughput | Bps (bits/second) | netThroughputMean, netThroughputSD | $\R_{\ge 0}$ (4-byte FP) |
| Difficulty | A ratio[^4] | difficulty | $\R_{\ge 0}$ (8-byte FP) |
| System times | milliseconds | simTime, sysTime[^5] | Positive integers |



[^1]: The standard deviation is the square root of the variance, which is in turn calculated as the probability-weighted sum of the squared differences between each datum and the expected value (for discrete distributions). Since probabilities are non-negative and the square of any real number is non-negative, the square root of a non-negative number must in turn be positive.  
$\sigma = \sqrt{\sum_{i=1}^{n} p_i{(x_i - \mu)}^2}$

[^2]: This is a stand-in for any arbitrary fiat (i.e. government backed) currency. E.g. USD, CAD, yen, etc.

[^3]: Kilowatt-hours are a non-SI unit, equivalent to the energy of 1000 watts over one hour, i.e. 3,600,000 joules.  

[^4]: The difficulty is expressed as the ratio of the search space over the success space, and can be considered as the *inverse* of the probability of a single hash succeeding. e.g. If a hash has a 0.5% chance of success, then the difficulty would be 200 (out of 200 hashes, 1 is expected to succeed).

[^5]: While both simTime and sysTime are denominated in milliseconds, simTime is measured as milliseconds since the start of the simulation and tracks the simulated passage of time in the simulation, whereas sysTime is measured as milliseconds since 00:00:00 UTC Jan 1, 1970 (i.e. [unix time](https://en.wikipedia.org/wiki/Unix_time)) and tracks the actual passage of time in the real world.