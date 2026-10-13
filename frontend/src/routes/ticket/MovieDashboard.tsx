import { Header } from '@root/components'
import { useNavigate } from 'react-router'
import { useListMovies } from '@root/hooks/movie/useListMovies.ts'

const MovieDashboard = () => {
  const navigate = useNavigate()
  const { data: movies, isLoading, isError } = useListMovies()

  return (
    <main className='all-users wrapper'>
      <Header
        title='Movie Dashboard'
        description='Show all the movies are playing'
      />

      {isLoading && (
        <div className='flex justify-center items-center py-20'>
          <p className='text-gray-500 text-lg font-medium'>
            Loading list of movies...
          </p>
        </div>
      )}

      {isError && (
        <div className='flex justify-center items-center py-20'>
          <p className='error'>Cannot load the list of movies.</p>
        </div>
      )}

      <div className='grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-7 mt-6'>
        {movies?.map((movie) => (
          <div
            key={movie.id}
            onClick={() => navigate(`/movies/${movie.id}`)}
            className='trip-card cursor-pointer transition-all duration-300 hover:shadow-200 hover:-translate-y-1'
          >
            <div className='w-full h-[160px] bg-primary-50 flex items-center justify-center rounded-t-xl'>
              <span className='text-primary-500 font-bold text-2xl'>
                {movie.title}
              </span>
            </div>

            <article className='p-5 flex flex-col gap-3'>
              <h2 className='text-lg font-semibold text-dark-100 line-clamp-1'>
                {movie.title}
              </h2>
              <p className='text-sm text-gray-100 line-clamp-2'>
                {movie.description}
              </p>

              <div className='pt-2 flex items-center justify-between'>
                <span className='text-xs font-medium px-2.5 py-1 bg-success-50 text-success-700 rounded-full'>
                  Now Showing
                </span>
                <span className='text-sm font-semibold text-primary-500'>
                  Book a ticket now &rarr;
                </span>
              </div>
            </article>
          </div>
        ))}
      </div>
    </main>
  )
}

export default MovieDashboard
