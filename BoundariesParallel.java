

    import java.util.concurrent.ForkJoinPool;
    import java.util.concurrent.RecursiveTask;

    /*
     * The following algorithm uses parallelism to find the edge values of our
     * census data. These include the maximum latitude, maximum longitude, minimum
     * latitude, minimum longitude, and total population.
     */

    public class BoundariesParallel extends RecursiveTask<BoundaryHolder>{

        public static final int CUTOFF = 1000;
        private static final long serialVersionUID = 1L;
        private CensusData result;
        private int low,high;

        //constructor
        private BoundariesParallel(CensusData result, int low, int high){
            this.result = result;
            this.low = low;
            this.high = high;
        }

   
       @Override
        //Computes the boundary data.
        protected BoundaryHolder compute() {
            BoundaryHolder data;
            if(high - low <= CUTOFF){//Base case

                //Initialize fields for purposes of comparison
                data = new BoundaryHolder(result.data[low].latitude,result.data[low].latitude,
                        result.data[low].longitude,result.data[low].longitude);
                data.population += result.data[low].population;

                //Compute the extremes in this subsection of our parallel algorithm.
                //In addition, update total population data.
                for(int i = low + 1; i < high; i++){
                    if(result.data[i].latitude > data.max_lat)
                        data.max_lat = result.data[i].latitude;
                    if(result.data[i].latitude < data.min_lat)
                        data.min_lat = result.data[i].latitude;
                    if(result.data[i].longitude > data.max_long)
                        data.max_long = result.data[i].longitude;
                    if(result.data[i].longitude < data.min_long)
                        data.min_long = result.data[i].longitude;
                    data.population += result.data[i].population;
                }

                return data;
            }

            else{//Recursive case
                BoundariesParallel left = new BoundariesParallel(result,low,(high+low)/2);
                BoundariesParallel right = new BoundariesParallel(result,(high+low)/2,high);
                left.fork();
                BoundaryHolder rightAns = right.compute();
                BoundaryHolder leftAns = left.join();
                return getNewBoundaries(leftAns,rightAns);

           }
        }

        private static final ForkJoinPool fjPool = new ForkJoinPool();

        /**
         * Finds the boundary data by using parallelism.
         */
        public static BoundaryHolder findBoundaries(CensusData result){
            return fjPool.invoke(new BoundariesParallel(result, 0, result.data_size));
        }

        //Used to merge the data of two BoundaryHolders
        private BoundaryHolder getNewBoundaries(BoundaryHolder c1, BoundaryHolder c2){
            float maxLat, minLat, maxLong, minLong;
            if (c1.max_lat > c2.max_lat)
                maxLat = c1.max_lat;
            else
                maxLat = c2.max_lat;
            if(c1.min_lat < c2.min_lat)
                minLat = c1.min_lat;
            else
                minLat = c2.min_lat;
            if(c1.max_long > c2.max_long)
                maxLong = c1.max_long;
            else
                maxLong = c2.max_long;
            if(c1.min_long < c2.min_long)
                minLong = c1.min_long;
            else
                minLong = c2.min_long;

            return new BoundaryHolder(maxLat,minLat,maxLong,minLong, c1.population + c2.population);

        }

    }

