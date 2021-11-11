#!/usr/bin/ruby

# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

# Requires: Ruby 1.9.3, Ant, Java 7, gnuplot, GNU utilities

require 'fileutils'

if ( ARGV.size < 1 )
  puts "Usage: ruby run_experiments.rb FILES...";
  Process.exit
end

puts "Compiling..."
`ant clean compile`

experiments = []

ERRORPREFIX = '"Illegal line #{linec} in file #{f}: "'
DISTRIBUTIONS = ["uniform", "exponential", "poisson", "pareto1.5", "pareto2", "pareto3"]
METHODS = /(Smallest|Greatest)Divisors|(Modified)?SainteLague|HarmonicMean|EqualProportions|Imperiali|Danish|LDM\(\d*\.?\d+,\d*\.?\d+\)/
METHODNAMES = ["SmallestDivisors", "GreatestDivisors", "ModifiedSainteLague", 
               "SainteLague", "HarmonicMean", "EqualProportions", "Imperiali", 
               "Danish", "LDM(double,double)"]

puts "Reading experiment definitions..."
ARGV.each { |f|
  if ( !File.exist?(f) )
    puts "No such file: #{f}"
  else
    File.open(f, "r") { |file|
      linec = 0
      
      file.readlines.each { |line|
        line.strip!
        linec += 1
        if ( line[0] == '#' || line.size == 0 )
          next
        end
      
        params = line.split(/\s+/)
        if ( params.size != 8 )
          puts eval(ERRORPREFIX) + "need seven columns."
          next
        end
        
        exp = [params[0]]
        (1..2).each { |i|
          if ( /\d+(,\d+)*/ =~ params[i] )
            exp.push(params[i])
          else
            puts eval(ERRORPREFIX) + "column #{i+1} needs to be a comma-separated list of integers."
            next
          end
        }
        
        (3..4).each { |i|
          if ( /\d+/ =~ params[i] )
            exp.push(params[i])
          else
            puts eval(ERRORPREFIX) + "column #{i+1} needs to be an integer."
            next
          end
        }
        
        if ( /\d+/ =~ params[5] )
          exp.push(params[5])
        elsif ( "NOW" == params[5] )
          exp.push((Time.now.to_r * 1000000).to_i.to_s)
        else
          puts eval(ERRORPREFIX) + "column 6 needs to be an integer or 'NOW'."
            next
        end
        
        if ( DISTRIBUTIONS.include?(params[6]) )
          exp.push(params[6])
        else
          puts eval(ERRORPREFIX) + "column 7 needs to be one of #{DISTRIBUTIONS.join(", ")}."
          next
        end
        
        if ( METHODS =~ params[7] )
          exp.push(params[7])
        else
          puts eval(ERRORPREFIX) + "column 8 needs to be one of #{METHODNAMES.join(", ")}."
          next
        end

        experiments.push(exp)
      }      
    }
  end
}

dir = "experiments_#{Time.now.strftime("%Y-%m-%d-%H:%M:%S")}"
Dir.mkdir(dir)
Dir.chdir(dir)

# Setup folder structure the Java program expects
FileUtils::mkdir_p(["tmp", "data", "plots/times", "plots/counters", 
                    "plots/scatter", "plots/averages"])

# Run and protocol everything
puts "Performing #{experiments.size} experiments..."
puts "\t(Follow progress with 'tail -f #{dir}/experiments.log')"
experiments.each { |e|
  `echo "#{e.join("\t")}" >> all.experiment`
  `java -cp ../build de.unikl.cs.agak.appportionment.experiments.RunningTimeMain #{e.map { |s| "\"#{s}\"" }.join(" ")} >> experiments.log 2>&1`
  `echo "\n\n\n" >> experiments.log`
}
`echo "Done." >> experiments.log`

# Prettify log
lines = []
File.open("experiments.log", "r") { |f|
  lines = f.readlines
}
lines.map! { |l| l.gsub("\33[1A\33[2K", " " * 8) }
File.open("experiments.log", "w") { |f|
  f.write(lines.join)
}

puts "Plotting..."
Dir["tmp/*.gp"].each { |gp|
  `gnuplot "#{gp}"`
}
