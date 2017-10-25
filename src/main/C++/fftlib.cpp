#include "../java/com/github/jbharter/com_github_jbharter_FasterFFT.h"
#include <iostream>

using namespace std;

jint Java_com_github_jbharter_FasterFFT_sendMeAnInt(JNIEnv * env, jobject object, jcharArray darr) {


    jchar *a[] = env->GetCharArrayRegion(env,0,5,darr);

        cout << a;

    return 2;
}
