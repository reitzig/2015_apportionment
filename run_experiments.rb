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

if ( ARGV.size < 1 )
  puts "Usage: ruby run_experiments.rb FILES...";
  Process.exit
end

puts "Compiling..."
`ant clean`
`ant compile`

experiments = []

ERRORPREFIX = '"Illegal line #{linec} in file #{f}: "'
DISTRIBUTIONS = ["uniform", "exponential"]

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
        if ( params.size != 9 )
          puts eval(ERRORPREFIX) + "need eight columns."
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
        
        (7..8).each { |i|
          if ( /\d+(\.\d+)?/ =~ params[i] )
            exp.push(params[i])
          else
            puts eval(ERRORPREFIX) + "column #{i+1} needs to be a float."
            next
          end
        }

        experiments.push(exp)
      }      
    }
  end
}

dir = "experiments_#{Time.now.strftime("%Y-%m-%d-%H:%M:%S")}"
Dir.mkdir(dir)
Dir.chdir(dir)
puts "Performing #{experiments.size} experiments..."
puts "\t(Follow progress with 'tail -f #{dir}/experiments.log')"
experiments.each { |e|
  `java -cp ../build RunningTimeMain #{e.join(" ")} >> experiments.log`
  `echo "\n\n\n" >> experiments.log`
}

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
Dir["*.gp"].each { |gp|
  `gnuplot #{gp}`
}
