# TransparentDiscord
A Discord client designed to mimic Google's Hangouts Transparent UI

<h3>Background</h3>
<p>After Google's decision to transition Hangouts from a consumer application to a business application, I felt it was
finally getting time to switch over to Discord. The problem is, I have never been a fan of full blown desktop applications
for chat clients, instead opting for Hangouts various floating UI styles. As far as I can tell, there is currently no easy
way to achieve a compact floating UI with Discord. So I set out to make my own, this being the result.</p>

<h3>About</h3>
<p>TransparentDiscord is beings developed using <a href="https://github.com/DV8FromTheWorld/JDA"><code>JDA</code></a>, a
Discord API wrapper for Java, as well as the built in Java Swing Toolkit. My hope is to provide a fully functional Discord
client in a compact, floating UI akin to the Google Hangouts Transparent UI. In its current state, TransparentDiscord is
capable of facilitating text communication via Private/Direct Messages that have already been created.</p>

<h3>Building</h3>
<p>I am currently using Gradle to build TransparentDiscord, as per the instructions found in
<a href="https://github.com/DV8FromTheWorld/JDA/wiki/2%29-Setup">the JDA Setup Guide</a>. After building with the shadowJar
Gradle Task, you will find the runnable jarfile in <code>build/libs</code>.</p>

<h3>Running</h3>
<p>TransparentDiscord requires you to input your Discord User Token. To obtain this token, go to the Discord web client. If
using Chrome, open the inspector (Ctrl + Shift + I), go to the Application tab, under Local Storage in the sidebar, select
https://discordapp.com, scroll to the bottom, and get the "token" value. After running the built jar file (<code>java
-jar TransparentDiscord-X.X-all.jar</code>), paste in the token and press enter. JDA will print some initialization messages,
then the UI will open.</p>

<h3>Plans</h3>
<p>TransparentDiscord is far from finished. Immidiate plans include:
<bl>
  <li>Adding file support</li>
  <li>Adding support for group chats</li>
  <li>Adding support for creating new channels</li>
  <li>Adding support for message reactions</li>
</bl><br>
Presently, the interface is using the default Swing appearance. Eventually that will need attention. A notification system
needs to be devised, as well as support for setting and displaying statuses. At some point down the road, I'd also
like to look into supporting voice calls.</p>

<h3>Disclaimer</h3>
<p>I am not in any way affiliated with the creators of Discord or the creators of JDA.</p>
  
