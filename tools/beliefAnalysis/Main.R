library(tidyverse)
library(nptest)

boot.lower <- function(x,y,Iter){
  npbs <- np.boot(x = x, statistic = y, R = Iter)
  return(ifelse(is.nan(npbs$bca[2,1]),0,npbs$bca[2,1]))
}

boot.upper <- function(x,y,Iter){
  npbs <- np.boot(x = x, statistic = y, R = Iter)
  return(ifelse(is.nan(npbs$bca[2,2]),0,npbs$bca[2,2]))
  return(npbs$bca[2,2])
}

experiment_forks = "2024.09.13 15.41.53"


Bootstrap_R = 499
experiment = "2024.09.13 23.27.53"

data <- read_csv(paste0("../../log/",experiment,"/BeliefLog - ",experiment,".csv"))
data <- data %>% rename(Simulation = SimID, Transaction = `Transaction ID`,Time = `Time (ms from start)`)
head(data)

net = data %>% group_by(Simulation,Transaction,Time) %>% summarise(conf = mean(Believes)) 
head(net)

#Aggregate over simulations
confs = net %>% group_by(Time,Transaction) %>% 
  summarise(avgConf = mean(conf), sd = sd(conf), medConf = median(conf), 
            lwr = boot.lower(conf,mean,Bootstrap_R),upr = boot.upper(conf,mean,Bootstrap_R),
            VaR = quantile(conf,0.05))

confs2plot <- confs %>% mutate(Condition = as.factor(Transaction))

ggplot(data = confs2plot, aes(x = Time, y=avgConf)) + 
  geom_line(aes(color = Condition)) + 
  geom_ribbon(aes(ymin=lwr,ymax=upr,fill = Condition),alpha=0.1) + 
  ylab("Confidence") +
  ggtitle(label = "Confidence in f over time (mean and 95% CI)") + 
  geom_hline(yintercept = 0.8)


View(confs2plot)
