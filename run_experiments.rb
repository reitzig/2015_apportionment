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
        if ( params.size != 8 )
          puts eval(ERRORPREFIX) + "need eight columns."
          next
        end
        
        exp = [params[0]]
        if ( /\d+(,\d+)*/ =~ params[1] )
          exp.push(params[1])
        else
          puts eval(ERRORPREFIX) + "column 2 needs to be a comma-separated list of integers."
          next
        end
        
        (2..3).each { |i|
          if ( /\d+/ =~ params[i] )
            exp.push(params[i])
          else
            puts eval(ERRORPREFIX) + "column #{i+1} needs to be an integer."
            next
          end
        }
        
        if ( /\d+/ =~ params[4] )
          exp.push(params[4])
        elsif ( "NOW" == params[4] )
          exp.push((Time.now.to_r * 1000000).to_i.to_s)
        else
          puts eval(ERRORPREFIX) + "column 5 needs to be an integer or 'NOW'."
            next
        end
        
        if ( DISTRIBUTIONS.include?(params[5]) )
          exp.push(params[5])
        else
          puts eval(ERRORPREFIX) + "column 6 needs to be one of #{DISTRIBUTIONS.join(", ")}."
          next
        end
        
        (6..7).each { |i|
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

dir = "experiments #{Time.now.strftime("%Y-%m-%d-%H:%M:%S")}"
Dir.mkdir(dir)
Dir.chdir(dir)
puts "Performing #{experiments.size} experiments..."
puts "\t(Follow progress with 'tail -f #{dir}/experiments.log')"
experiments.each { |e|
  `java -cp ../build RunningTimeMain #{experiments.join(" ")} >> experiments.log`
}
puts "Plotting..."
Dir["*.gp"].each { |gp|
  `gnuplot #{gp}`
}
