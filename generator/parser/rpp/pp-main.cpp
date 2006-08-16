/*
  Copyright 2005 Roberto Raggi <roberto@kdevelop.org>

  Permission to use, copy, modify, distribute, and sell this software and its
  documentation for any purpose is hereby granted without fee, provided that
  the above copyright notice appear in all copies and that both that
  copyright notice and this permission notice appear in supporting
  documentation.

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
  KDEVELOP TEAM BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
  AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

#include "pp.h"

using namespace rpp;

#ifndef GCC_MACHINE
#  define GCC_MACHINE "i386-redhat-linux"
#endif

#ifndef GCC_VERSION
#  define GCC_VERSION "4.1.1"
#endif

void usage ()
{
  std::cerr << "usage: rpp file.cpp" << std::endl;
  ::exit (EXIT_FAILURE);
}

int main (int, char *argv [])
{
  const char *input_file = 0;
  bool opt_help = false;
  bool opt_dump_macros = false;

  pp_environment env;
  pp preprocess(env);

  std::string result;
  result.reserve (20 * 1024); // 20K

  pp_output_iterator<std::string> out (result);
  pp_null_output_iterator null_out;

  preprocess.push_include_path ("/usr/include");
  preprocess.push_include_path ("/usr/lib/gcc/" GCC_MACHINE "/" GCC_VERSION "/include");

  preprocess.push_include_path ("/usr/include/c++/" GCC_VERSION);
  preprocess.push_include_path ("/usr/include/c++/" GCC_VERSION "/" GCC_MACHINE);

  while (const char *arg = *++argv)
    {
      if (arg [0] != '-')
        input_file = arg;

      else if (! strncmp (arg, "-help", 5))
        opt_help = true;

      else if (! strncmp (arg, "-dM", 3))
        opt_dump_macros = true;

      else if (! strncmp (arg, "-include", 8))
        {
          if (argv [1])
            preprocess.file (*++argv, null_out);
        }

      else if (! strncmp (arg, "-conf", 8))
        {
          if (argv [1])
            preprocess.file (*++argv, null_out);
        }

      else if (! strncmp (arg, "-I", 2))
        {
          arg += 2;

          if (! arg [0] && argv [1])
            arg = *++argv;

          if (arg)
            preprocess.push_include_path (arg);
        }

      else if (! strncmp (arg, "-U", 2))
        {
          arg += 2;

          if (! arg [0] && argv [1])
            arg = *++argv;

          if (arg)
            {
              pp_fast_string tmp (arg, strlen (arg));
              env.unbind (&tmp);
            }
        }

      else if (! strncmp (arg, "-D", 2))
        {
          arg += 2;

          if (! arg [0] && argv [1])
            arg = *++argv;

          if (arg)
            std::cerr << "*** WARNING -D not implemented" << std::endl;
        }
    }

  if (! input_file || opt_help)
    {
      usage ();
      return EXIT_FAILURE;
    }

  if (opt_dump_macros)
    {
      preprocess.file (input_file, null_out);

      for (pp_environment::const_iterator it = env.first_macro (); it != env.last_macro (); ++it)
        {
          pp_macro const *m = *it;

          if (m->hidden)
            continue;

          std::string id (m->name->begin (), m->name->end ());
          std::cout << "#define " << id;

          if (m->function_like)
            {
              std::cout << "(";

              for (std::size_t i = 0; i < m->formals.size (); ++i)
                {
                  if (i != 0)
                    std::cout << ", ";

                  pp_fast_string const *f = m->formals [i];
                  std::string name (f->begin (), f->end ());
                  std::cout << name;
                }

              if (m->variadics)
                std::cout << "...";

              std::cout << ")";
            }

          std::cout << "\t";
          std::string def (m->definition->begin (), m->definition->end ());
          std::cout << def;
          std::cout << std::endl;
        }
    }
  else
    {
      preprocess.file (input_file, out);
      std::cout << result;
    }

  return EXIT_SUCCESS;
}

// kate: space-indent on; indent-width 2; replace-tabs on;

