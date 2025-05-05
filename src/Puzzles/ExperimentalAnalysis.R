library(dplyr)
library(tidyr)
library(ggplot2)

data <- rbind(
  read.csv("test_results_5x5.csv"),
  read.csv("test_results_6x6.csv"),
  read.csv("test_results_7x7.csv"),
  read.csv("test_results_8x8.csv"),
  read.csv("test_results_9x9.csv"),
  read.csv("test_results_10x10.csv"),
  read.csv("test_results_11x11.csv")
)

# Condense Data to Average of Trials
comp_data <- data %>% 
  group_by(SIZE, PUZZLE_ID) %>%
  summarise(
    EXECUTION_TIME = round(mean(EXECUTION_TIME), digits = 2),
    SUCCESS = sum(SUCCESS == "true") > 1,
    NODES_EXPANDED = first(NODES_EXPANDED),
    MAX_PATH_LENGTH = first(MAX_PATH_LENGTH),
    NUM_AGENTS = first(NUM_AGENTS)
  ) %>%
  mutate(EXECUTION_TIME = if_else(EXECUTION_TIME >= 180000, 180000, EXECUTION_TIME))



# Scatter Plot of Time vs. Size
ggplot(comp_data, aes(x = SIZE, y = EXECUTION_TIME)) + 
  geom_point(aes(color = "Data")) +
  stat_summary(
    fun = mean, 
    geom = "line", 
    aes(group = 1, color = "Mean Execution Time"),
    size = 1.2
  ) +
  scale_color_manual(
    name = "Legend",
    values = c("Data" = "black", "Mean Execution Time" = "red")
  ) +
  scale_y_log10() +
  scale_x_continuous(breaks = unique(comp_data$SIZE)) +
  labs(
    title = "Execution Time by Problem Size",
    x = "Problem Size",
    y = "Execution Time (log scale)"
  ) +
  theme_minimal(base_size = 12) +
  theme(
    panel.border = element_rect(color = "black", fill = NA, linewidth = 0.8),
    legend.position = "top",
    plot.title = element_text(hjust = 0.5, face = "bold"),
    axis.text.x = element_text(hjust = 1)
  )

# Line Plot Time vs Size
ggplot(comp_data, aes(x = SIZE, y = EXECUTION_TIME)) + 
  # Mean line (red)
  stat_summary(
    fun = mean, 
    geom = "line", 
    aes(color = "Mean Execution Time"), 
    size = 1.2
  ) +
  # Error bars (min/max range, dark blue)
  stat_summary(
    fun.data = function(x) c(y = mean(x), ymin = min(x), ymax = max(x)),
    geom = "errorbar",
    aes(color = "Min/Max Range"),
    width = 0.2,
    size = 0.8
  ) +
  scale_color_manual(
    name = "Legend",
    values = c("Mean Execution Time" = "red", "Min/Max Range" = "darkblue")
  ) +
  scale_x_continuous(breaks = min(comp_data$SIZE):max(comp_data$SIZE)) +
  scale_y_log10() +
  labs(
    title = "Execution Time by Puzzle Size",
    x = "Puzzle Size",
    y = expression("Execution Time (log"["  10"]*" ms)")
  ) +
  theme_minimal(base_size = 12) +
  theme(
    panel.border = element_rect(color = "black", fill = NA, linewidth = 0.8),
    plot.title = element_text(hjust = 0.5, face = "bold", size = 20), # Bigger title
    axis.title.x = element_text(size = 18), # Bigger x-axis label
    axis.title.y = element_text(size = 18), # Bigger y-axis label
    axis.text.x = element_text(hjust = 1, size = 16), # Adjust x-axis tick labels
    axis.text.y = element_text(size = 16), # Adjust y-axis tick labels
    legend.position = "top"
  )



# Line Plot of Time vs. Max Path Length
ggplot(comp_data, aes(x = MAX_PATH_LENGTH, y = EXECUTION_TIME)) + 
  # Max execution time (red solid)
  stat_summary(
    fun = max, 
    geom = "line", 
    aes(group = 1, color = "Maximum Execution Time", linetype = "Maximum Execution Time"),
    size = 1.1
  ) +
  # Mean execution time (orange dashed)
  stat_summary(
    fun = mean, 
    geom = "line", 
    aes(group = 1, color = "Mean Execution Time", linetype = "Mean Execution Time"),
    size = 1.2
  ) +
  # Min execution time (yellow solid)
  stat_summary(
    fun = min, 
    geom = "line", 
    aes(group = 1, color = "Minimum Execution Time", linetype = "Minimum Execution Time"),
    size = 1.1
  ) +
  scale_color_manual(
    name = "Legend",
    values = c(
      "Mean Execution Time" = "red",
      "Maximum Execution Time" = "blue",
      "Minimum Execution Time" = "dodgerblue"
    )
  ) +
  scale_linetype_manual(
    name = "Legend",
    values = c(
      "Mean Execution Time" = "dashed",
      "Maximum Execution Time" = "solid",
      "Minimum Execution Time" = "solid"
    )
  ) +
  scale_x_continuous(breaks = min(comp_data$MAX_PATH_LENGTH):max(comp_data$MAX_PATH_LENGTH)) +
  labs(
    title = "Execution Time by Maximum Path Length",
    x = "Maximum Path Length",
    y = expression("Execution Time (log"["  10"]*" ms)")
  ) +
  theme_minimal(base_size = 12) +
  theme(
    panel.border = element_rect(color = "black", fill = NA, linewidth = 0.8),
    plot.title = element_text(hjust = 0.5, face = "bold", size = 20), # Bigger title
    axis.title.x = element_text(size = 18), # Bigger x-axis label
    axis.title.y = element_text(size = 18), # Bigger y-axis label
    axis.text.x = element_text(hjust = 1, size = 16), # Adjust x-axis tick labels
    axis.text.y = element_text(size = 16), # Adjust y-axis tick labels
    legend.position = "top"
  )

ggplot(comp_data, aes(x = MAX_PATH_LENGTH, y = EXECUTION_TIME)) +
  stat_summary(fun.min = min, fun.max = max, geom = "ribbon", fill = "red", alpha = 0.3) +
  stat_summary(fun = mean, geom = "line", color = "red", linetype = "dashed", size = 1.2) +
  labs(title = "Execution Time by Maximum Path Length",
       x = "Maximum Path Length",
       y = "Execution Time (log scale)") +
  theme_minimal()

ggplot(comp_data, aes(x = MAX_PATH_LENGTH, y = EXECUTION_TIME)) + 
  stat_summary(
    fun = mean,
    fun.min = min,
    fun.max = max,
    geom = "ribbon",
    fill = "blue",
    alpha = 0.3
  ) +
  stat_summary(
    fun = mean,
    geom = "line",
    color = "blue",
    size = 1.2
  ) +
  scale_x_continuous(breaks = min(comp_data$MAX_PATH_LENGTH):max(comp_data$MAX_PATH_LENGTH)) +
  labs(
    title = "Execution Time by Maximum Path Length",
    x = "Maximum Path Length",
    y = "Execution Time (log scale)"
  ) +
  theme_minimal(base_size = 12) +
  theme(
    panel.border = element_rect(color = "black", fill = NA, linewidth = 0.8),
    plot.title = element_text(hjust = 0.5, face = "bold"),
    axis.text.x = element_text(hjust = 1)
  )


# Line Plot Time vs Max Path Length with Error Bars
ggplot(comp_data, aes(x = MAX_PATH_LENGTH, y = EXECUTION_TIME)) + 
  # Mean line (red)
  stat_summary(
    fun = mean, 
    geom = "line", 
    aes(color = "Mean Execution Time"), 
    size = 1.2
  ) +
  # Error bars (min/max range, dark blue)
  stat_summary(
    fun.data = function(x) c(y = mean(x), ymin = min(x), ymax = max(x)),
    geom = "errorbar",
    aes(color = "Min/Max Range"),
    width = 0.2,
    size = 0.8
  ) +
  scale_color_manual(
    name = "Legend",
    values = c("Mean Execution Time" = "red", "Min/Max Range" = "darkblue")
  ) +
  scale_x_continuous(
    breaks = seq(min(comp_data$MAX_PATH_LENGTH), max(comp_data$MAX_PATH_LENGTH), by = 2)
  ) +
  scale_y_log10() +
  labs(
    title = "Execution Time by Maximum Path Length",
    x = "Maximum Path Length",
    y = expression("Execution Time (log"["10"]*" ms)")
  ) +
  theme_minimal(base_size = 12) +
  theme(
    panel.border = element_rect(color = "black", fill = NA, linewidth = 0.8),
    plot.title = element_text(hjust = 0.5, face = "bold", size = 20),
    axis.title.x = element_text(size = 18),
    axis.title.y = element_text(size = 18),
    axis.text.x = element_text(hjust = 1, size = 16),
    axis.text.y = element_text(size = 16),
    legend.position = "top"
  )

# Line Plot Time vs Num Agents with Error Bars
ggplot(comp_data, aes(x = NUM_AGENTS, y = EXECUTION_TIME)) + 
  # Mean line (red)
  stat_summary(
    fun = mean, 
    geom = "line", 
    aes(color = "Mean Execution Time"), 
    size = 1.2
  ) +
  # Error bars (min/max range, dark blue)
  stat_summary(
    fun.data = function(x) c(y = mean(x), ymin = min(x), ymax = max(x)),
    geom = "errorbar",
    aes(color = "Min/Max Range"),
    width = 0.2,
    size = 0.8
  ) +
  scale_color_manual(
    name = "Legend",
    values = c("Mean Execution Time" = "red", "Min/Max Range" = "darkblue")
  ) +
  scale_x_continuous(breaks = min(comp_data$NUM_AGENTS):max(comp_data$NUM_AGENTS)) +
  scale_y_log10() +
  labs(
    title = "Execution Time by Number of Agents",
    x = "Number of Agents",
    y = "Execution Time (log"["  10"]*" ms)"
  ) +
  theme_minimal(base_size = 12) +
  theme(
    panel.border = element_rect(color = "black", fill = NA, linewidth = 0.8),
    plot.title = element_text(hjust = 0.5, face = "bold"),
    axis.text.x = element_text(hjust = 1),
    legend.position = "top"
  )


# Line Plot of Time vs. Number of Agents
ggplot(comp_data, aes(x = NUM_AGENTS, y = EXECUTION_TIME)) + 
  stat_summary(
    fun = max, 
    geom = "line", 
    aes(group = 1, color = "Maximum Execution Time", linetype = "Maximum Execution Time"),
    size = 1.2
  ) +
  stat_summary(
    fun = mean, 
    geom = "line", 
    aes(group = 1, color = "Mean Execution Time", linetype = "Mean Execution Time"),
    size = 1.2
  ) +
  scale_color_manual(
    name = "Legend",
    values = c("Mean Execution Time" = "red", "Maximum Execution Time" = "red")
  ) +
  scale_linetype_manual(
    name = "Legend",
    values = c("Mean Execution Time" = "dashed", "Maximum Execution Time" = "solid")
  ) +
  scale_x_continuous(breaks = c(min(comp_data$NUM_AGENTS):max(comp_data$NUM_AGENTS))) +
  labs(
    title = "Execution Time by Number of Agents",
    x = "Number of Agents",
    y = "Execution Time (log scale)"
  ) +
  theme_minimal(base_size = 12) +
  theme(
    panel.border = element_rect(color = "black", fill = NA, linewidth = 0.8),
    legend.position = "top",
    plot.title = element_text(hjust = 0.5, face = "bold"),
    axis.text.x = element_text(hjust = 1)
  )




# Scatter Plot of Nodes Expanded vs. Size
ggplot(comp_data, aes(x = SIZE, y = NODES_EXPANDED)) + 
  geom_point(aes(color = "Data")) +
  stat_summary(
    fun = mean, 
    geom = "line", 
    aes(group = 1, color = "Mean Execution Time"),
    size = 1.2
  ) +
  scale_color_manual(
    name = "Legend",
    values = c("Data" = "black", "Mean Execution Time" = "red")
  ) +
  scale_y_log10() +
  scale_x_continuous(breaks = unique(comp_data$SIZE)) +
  labs(
    title = "Execution Time by Problem Size",
    x = "Problem Size",
    y = "Execution Time (log scale)"
  ) +
  theme_minimal(base_size = 12) +
  theme(
    panel.border = element_rect(color = "black", fill = NA, linewidth = 0.8),
    legend.position = "top",
    plot.title = element_text(hjust = 0.5, face = "bold"),
    axis.text.x = element_text(hjust = 1)
  )

ggplot(comp_data, aes(x = SIZE, y = NODES_EXPANDED)) + 
  # Mean line (red)
  stat_summary(
    fun = mean, 
    geom = "line", 
    aes(color = "Mean Execution Time"), 
    size = 1.2
  ) +
  # Error bars (min/max range, dark blue)
  stat_summary(
    fun.data = function(x) c(y = mean(x), ymin = min(x), ymax = max(x)),
    geom = "errorbar",
    aes(color = "Min/Max Range"),
    width = 0.2,
    size = 0.8
  ) +
  scale_color_manual(
    name = "Legend",
    values = c("Mean Nodes Expanded" = "red", "Min/Max Range" = "darkblue")
  ) +
  scale_x_continuous(breaks = min(comp_data$SIZE):max(comp_data$SIZE)) +
  scale_y_log10() +
  labs(
    title = "Nodes Expanded by Puzzle Size",
    x = "Sizes",
    y = expression("Nodes Expanded (log"["  10"]*" ms)")
  ) +
  theme_minimal(base_size = 12) +
  theme(
    panel.border = element_rect(color = "black", fill = NA, linewidth = 0.8),
    plot.title = element_text(hjust = 0.5, face = "bold"),
    axis.text.x = element_text(hjust = 1),
    legend.position = "top"
  )
