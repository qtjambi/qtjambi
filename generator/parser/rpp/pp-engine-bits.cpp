
#include "rpp/pp-engine-bits.h"
#include "rpp/pp-cctype.h"

rpp::pp::pp ( pp_environment &__env ) :
        env ( __env ), expand ( env )
{
    iflevel = 0;
    _M_skipping[iflevel] = 0;
    _M_true_test[iflevel] = 0;
}

std::back_insert_iterator<std::vector<std::string> > rpp::pp::include_paths_inserter ()
{
    return std::back_inserter ( rpp::pp::include_paths );
}

std::vector<std::string>::iterator rpp::pp::include_paths_begin ()
{
    return rpp::pp::include_paths.begin ();
}

std::vector<std::string>::iterator rpp::pp::include_paths_end ()
{
    return rpp::pp::include_paths.end ();
}

std::vector<std::string>::const_iterator rpp::pp::include_paths_begin () const
{
    return rpp::pp::include_paths.begin ();
}

std::vector<std::string>::const_iterator rpp::pp::include_paths_end () const
{
    return rpp::pp::include_paths.end ();
}

bool rpp::pp::is_absolute(std::string const &filename) const
{
#if defined(PP_OS_WIN)
    return filename.length() >= 3
           && filename.at(1) == ':'
           && (filename.at(2) == '\\' || filename.at(2) == '/');
#else
    return filename.length() >= 1
           && filename.at(0) == '/';
#endif
}

bool rpp::pp::file_exists (std::string const &__filename) const
{
    struct stat __st;
#if defined(PP_OS_WIN)
    return stat(__filename.c_str (), &__st) == 0;
#else
    return lstat (__filename.c_str (), &__st) == 0;
#endif
}

bool rpp::pp::file_isdir (std::string const &__filename) const
{
    struct stat __st;
#if defined(PP_OS_WIN)
    if (stat(__filename.c_str (), &__st) == 0)
        return (__st.st_mode & _S_IFDIR) == _S_IFDIR;
    else
        return false;
#else
    if (lstat (__filename.c_str (), &__st) == 0)
        return (__st.st_mode & S_IFDIR) == S_IFDIR;
    else
        return false;
#endif
}

void rpp::pp::push_include_path ( std::string const &path )
{
    if ( path.empty () || path [path.size () - 1] != PATH_SEPARATOR )
    {
        std::string tmp ( path );
        tmp += PATH_SEPARATOR;
        rpp::pp::include_paths.push_back ( tmp );
    } else {
        rpp::pp::include_paths.push_back ( path );
    }
}

bool rpp::pp::test_if_level()
{
    bool result = !rpp::pp::_M_skipping[rpp::pp::iflevel++];
    rpp::pp::_M_skipping[rpp::pp::iflevel] = rpp::pp::_M_skipping[rpp::pp::iflevel - 1];
    rpp::pp::_M_true_test[rpp::pp::iflevel] = false;
    return result;
}

int rpp::pp::skipping() const
{
    return rpp::pp::_M_skipping[rpp::pp::iflevel];
}

rpp::PP_DIRECTIVE_TYPE rpp::pp::find_directive ( char const *__directive, std::size_t __size ) const
{
    switch ( __size )
    {
    case 2:
        if ( __directive[0] == 'i'
                && __directive[1] == 'f' )
            return PP_IF;
        break;

    case 4:
        if ( __directive[0] == 'e' && !strcmp ( __directive, "elif" ) )
            return PP_ELIF;
        else if ( __directive[0] == 'e' && !strcmp ( __directive, "else" ) )
            return PP_ELSE;
        break;

    case 5:
        if ( __directive[0] == 'i' && !strcmp ( __directive, "ifdef" ) )
            return PP_IFDEF;
        else if ( __directive[0] == 'u' && !strcmp ( __directive, "undef" ) )
            return PP_UNDEF;
        else if ( __directive[0] == 'e' )
        {
            if ( !strcmp ( __directive, "endif" ) )
                return PP_ENDIF;
            else if ( !strcmp ( __directive, "error" ) )
                return PP_ERROR;
        }
        break;

    case 6:
        if ( __directive[0] == 'i' && !strcmp ( __directive, "ifndef" ) )
            return PP_IFNDEF;
        else if ( __directive[0] == 'd' && !strcmp ( __directive, "define" ) )
            return PP_DEFINE;
        else if ( __directive[0] == 'p' && !strcmp ( __directive, "pragma" ) )
            return PP_PRAGMA;
        break;

    case 7:
        if ( __directive[0] == 'i' && !strcmp ( __directive, "include" ) )
            return PP_INCLUDE;
        else if ( __directive[0] == 'w' && !strcmp ( __directive, "warning" ) )
            return PP_WARNING;
        break;

    case 12:
        if ( __directive[0] == 'i' && !strcmp ( __directive, "include_next" ) )
            return PP_INCLUDE_NEXT;
        break;

    default:
        break;
    }
    std::cerr << "** WARNING unknown directive '#" << __directive << "' at " << env.current_file << ":" << env.current_line << std::endl;
    return PP_UNKNOWN_DIRECTIVE;
}


FILE *rpp::pp::find_include_file ( std::string const &__input_filename, std::string *__filepath,
                                   INCLUDE_POLICY __include_policy, bool __skip_current_path ) const
{
    assert ( __filepath != 0 );
    assert ( ! __input_filename.empty() );

    __filepath->assign ( __input_filename );

    if ( is_absolute ( *__filepath ) )
        return std::fopen ( __filepath->c_str(), "r" );

    if ( ! env.current_file.empty () )
        _PP_internal::extract_file_path ( env.current_file, __filepath );

    if ( __include_policy == INCLUDE_LOCAL && ! __skip_current_path )
    {
        std::string __tmp ( *__filepath );
        __tmp += __input_filename;

        if ( file_exists ( __tmp ) && !file_isdir ( __tmp ) )
        {
            __filepath->append ( __input_filename );
            return std::fopen ( __filepath->c_str (), "r" );
        }
    }

    std::vector<std::string>::const_iterator it = include_paths.begin ();

    if ( __skip_current_path )
    {
        it = std::find ( include_paths.begin (), include_paths.end (), *__filepath );

        if ( it != include_paths.end () )
            ++it;

        else
            it = include_paths.begin ();
    }

    for ( ; it != include_paths.end (); ++it )
    {
        if ( __skip_current_path && it == include_paths.begin() )
            continue;

        __filepath->assign ( *it );
        __filepath->append ( __input_filename );

        if ( file_exists ( *__filepath ) && !file_isdir ( *__filepath ) )
            return std::fopen ( __filepath->c_str(), "r" );
    }

    return 0;
}
