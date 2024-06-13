# Package names
packages <- c("readr","tidyverse","visNetwork","tokenizers")

# Install packages not yet installed
installed_packages <- packages %in% rownames(installed.packages())
if (any(installed_packages == FALSE)) {
  install.packages(packages[!installed_packages])
}
# Packages loading
invisible(lapply(packages, library, character.only = TRUE))


referenceTime = "2022-03-21 14:20:00"

ms_to_date = function(ms, t0=referenceTime, timezone = "EST") {
  ## @ms: a numeric vector of milliseconds (big integers of 13 digits)
  ## @t0: a string of the format "yyyy-mm-dd", specifying the date that
  ##      corresponds to 0 millisecond
  ## @timezone: a string specifying a timezone that can be recognized by R
  ## return: a POSIXct vector representing calendar dates and times        
  sec = ms / 1000
  as.POSIXct(sec, origin=t0,timezone)
}

findHeight <- function(node,df,height){
  parents = df %>% filter(from == node) %>% pull(to)
  allheights = NULL
  for (i in parents){
    allheights = c(allheights,findHeight(i,df,height))
    min(allheights)
  }
  if (length(allheights)>0)
    return (max(allheights) + 1)
  else return (height)
}



path = "../log/"
runID = read_csv(paste0(path,"LatestFileName.txt"),col_names = FALSE)[[1]]
path = paste0("../log/",runID,"/")


vizTangle <- function(Node, tangleStateRaw, t) {

  if (t != -1) {
    t = max(tangleStateRaw %>% filter(SimTime <= t) %>% pull(SimTime))
  } else {
    t = max(tangleStateRaw %>% pull(SimTime))
  }
        
    tangleState <- tangleStateRaw %>% 
      pivot_longer(cols = c(Parent1, Parent2),names_to = "Parent",values_to = "to") %>%
      filter(NodeID == Node, SimTime == t) %>% rename("from" = "TransID") %>% 
      select(from,to,Weight,level,isTip) %>% mutate(arrows = "to") 

  
  
  edges =data.frame(tangleState %>% filter(to!=-1) %>% select(from,to))
  
  nodes = data.frame(tangleState %>% select(from,Weight,level,isTip) %>%
                       group_by(from,level,Weight) %>% summarise(desc = max(isTip)) %>%
                       rename("id" = "from") %>%
                       mutate (title = paste0("Site:",id," <b>(",Weight,")</b>"),
                               label = paste0(id," (",Weight,")"),
                               level = level,
                               shape = ifelse(desc,"star",ifelse(id ==0,"square","circle")))
  )
  
  return(visNetwork(nodes, edges, main = paste0("Node:", Node, " Time: ",t)) %>% visHierarchicalLayout(direction = "LR",
                                                     levelSeparation = 100,
                                                     nodeSpacing = 100,
                                                     parentCentralization = TRUE) %>%
    visPhysics(stabilization = TRUE)  %>%
    visEdges(arrows = "to") %>% 
    visIgraphLayout())
}


getNextEvent <- function (evt, t) {
  return (evt %>% filter(SimTime > t) %>% head(1))
}





