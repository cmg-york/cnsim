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

Bootstrap_R = 499
experiment = "2024.11.29 08.54.22"

data <- read_csv(paste0("../../log/",experiment,"/BeliefLog - ",experiment,".csv"))
data <- data %>% rename(Simulation = SimID, Transaction = `Transaction ID`,Time = `Time (ms from start)`)
net_pre = data %>% group_by(Simulation,Transaction,Time) %>% summarise(conf = mean(Believes)) 
endTime = min(net %>% group_by(Simulation) %>% summarise(maxTime = max(Time)) %>% select(maxTime))
net = net_pre %>% filter(Time<=endTime)


#Aggregate over simulations
confs = net %>% group_by(Time,Transaction) %>% 
  summarise(avgConf = mean(conf), sd = sd(conf), medConf = median(conf)) 
#            lwr = boot.lower(conf,mean,Bootstrap_R),upr = boot.upper(conf,mean,Bootstrap_R),
#            VaR = quantile(conf,0.05))


confs2plot <- confs %>% mutate(Transaction = as.factor(Transaction)) %>%
  mutate(Time = Time / 60000)
  #ggplot(aes(x = Time, y=conf)) + 
ggplot(data = confs2plot, aes(x = Time, y=avgConf, label = avgConf)) + 
  geom_line(aes(color = Transaction)) + 
  #geom_ribbon(aes(ymin=lwr,ymax=upr,fill = Condition),alpha=0.1) + 
  ylab("Confidence") + xlab("Time (min)") +
  xlim(0,28) +
  ggtitle(label = "Confidence in f over time") + 
  geom_hline(yintercept = 0.8)


tail(confs %>% filter(Transaction == 19),100)




#
# A D V A N C E D
#



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
