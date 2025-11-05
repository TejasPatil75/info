import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {

    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, IntWritable> {

        private static final IntWritable ONE = new IntWritable(1);
        private final Text word = new Text();

        @Override
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                String token = itr.nextToken()
                                  .replaceAll("[^a-zA-Z]", "")
                                  .toLowerCase();
                if (!token.isEmpty()) {
                    word.set(token);
                    context.write(word, ONE);
                }
            }
        }
    }

    public static class IntSumReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {

        private final IntWritable result = new IntWritable();

        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            int sum = 0;
            for (IntWritable v : values) sum += v.get();
            result.set(sum);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: WordCount <input> <output>");
            System.exit(2);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(WordCount.class);

        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);   // acts as a local reducer
        job.setReducerClass(IntSumReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}


// pom.xml file -

// <project xmlns="http://maven.apache.org/POM/4.0.0"
//          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
//          xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
//                              http://maven.apache.org/xsd/maven-4.0.0.xsd">
//     <modelVersion>4.0.0</modelVersion>
//     <groupId>org.example</groupId>
//     <artifactId>wordcount</artifactId>
//     <version>1.0</version>

//     <properties>
//         <hadoop.version>3.2.1</hadoop.version>
//         <maven.compiler.source>1.8</maven.compiler.source>
//         <maven.compiler.target>1.8</maven.compiler.target>
//     </properties>

//     <dependencies>
//         <dependency>
//             <groupId>org.apache.hadoop</groupId>
//             <artifactId>hadoop-client</artifactId>
//             <version>${hadoop.version}</version>
//         </dependency>
//     </dependencies>

//     <build>
//         <plugins>
//             <plugin>
//                 <groupId>org.apache.maven.plugins</groupId>
//                 <artifactId>maven-shade-plugin</artifactId>
//                 <version>3.2.4</version>
//                 <executions>
//                     <execution>
//                         <phase>package</phase>
//                         <goals>
//                             <goal>shade</goal>
//                         </goals>
//                     </execution>
//                 </executions>
//             </plugin>
//         </plugins>
//     </build>
// </project>