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

<h3>Downloads</h3>
<p>Downloads for the most recent development version can be found in the <code>Prebuilt</code> directory. For more stable versions,
check the <a href="https://github.com/MCPlummet/TransparentDiscord/releases">releases</a> page.</p>

<h3>Running</h3>
<p>TransparentDiscord requires you to input your Discord User Token. To obtain this token, go to the Discord web client. If
using Chrome, open the inspector (Ctrl + Shift + I), go to the Application tab, under Local Storage in the sidebar, select
https://discordapp.com, scroll to the bottom, and get the "token" value. After running the built jar file (<code>java
-jar TransparentDiscord-X.X-all.jar</code>), paste in the token into the login window and click login. If you check remember,
your token will be saved in a text file in the same directory as the jar. Be advised that the token is currently stored in plain text.</p>

<h3>Plans</h3>
<p>TransparentDiscord is far from finished. Immidiate plans include:
<bl>
  <li>Adding file/image upload support</li>
  <li>Adding support for creating new groups</li>
  <li>Message notifications</li>
  <li>UI Improvements</li>
</bl><br>
Presently, the interface is using the default Swing appearance. Eventually that will need attention. At some point down the road,
I'd also like to look into supporting voice calls, as well as implementing more of Discord's features, like custom emoji and message
reactions.</p>

<h3>Disclaimer</h3>
<p>I am not in any way affiliated with the creators of Discord or the creators of JDA.</p>
  
