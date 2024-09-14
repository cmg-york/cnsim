source("Library.R")


replay <- function(){
  t = 0
  realtime = ""
  
  tanglelog <- read_csv(paste0(path,"TangleLog - ",as.character(runID),".csv"))
  evtlog <- read_csv(paste0(path,"EventLog - ",as.character(runID),".csv")) %>% 
    mutate(RealSimTime = ms_to_date(SimTime)) %>%
    left_join(tanglelog, by = c("Object" = "Transaction","Node" = "Node","SimTime" = "SimTime")) %>% 
    arrange(SimTime)
  tangleState <- read_csv(paste0(path,"TangleState - ",as.character(runID),".csv"))
  
  
  z = "hello"
  while (z!= "exit"){
    z <- tokenize_words(readline(paste0(realtime,">")))

    switch(z[[1]][1], 
           "exit"={
             # case 'foo' here...
             cat('Exiting')
           },
           "n" ={
             evt = getNextEvent(evtlog,t)
             if (nrow(evt)>0) {
             t = evt$SimTime
             realtime = evt$RealSimTime
             print(evt %>% select(EventID, Node, EventType, Object, Parent1, Parent2,SimTime))
             } else {
               cat('End of Records')  
               z="exit"
             }
             
           },
           "look" ={
             cat('Investigating Tangle',z[[1]][2])
             print(vizTangle(z[[1]][2],tangleState,t))
           },
           {
             print('Unknown Command')
           }
           ) 
  }
}

replay()



