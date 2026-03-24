<h1>EasyHologram API</h1>

<p><strong>Lightweight server-side hologram library for Minecraft 1.21 (Fabric)</strong></p>
<p>Create floating text, blocks, and items in the world using vanilla Display Entities. One line of code — no packets, no hacks.</p>

<hr>

<h2>✨ Features</h2>
<ul>
  <li><strong>Text Holograms</strong> — floating text with colors, shadows, backgrounds</li>
  <li><strong>Block Holograms</strong> — any block floating in mid-air</li>
  <li><strong>Item Holograms</strong> — floating items with billboard mode</li>
  <li><strong>Animations</strong> — float, rotate, pulse presets</li>
  <li><strong>Auto-Save</strong> — holograms persist across restarts</li>
  <li><strong>JSON Export/Import</strong> — manually edit layouts</li>
  <li><strong>Events</strong> — Fabric events for mod interoperability</li>
  <li><strong>Debug Commands</strong> — full in-game command suite</li>
</ul>

<hr>

<h2>🔧 Commands</h2>
<p>All commands require OP level 2.</p>
<table>
  <tr><th>Command</th><th>Description</th></tr>
  <tr><td><code>/hologram create text &lt;x&gt; &lt;y&gt; &lt;z&gt; &lt;text&gt;</code></td><td>Create text hologram</td></tr>
  <tr><td><code>/hologram create block &lt;x&gt; &lt;y&gt; &lt;z&gt; &lt;block&gt;</code></td><td>Create block hologram</td></tr>
  <tr><td><code>/hologram create item &lt;x&gt; &lt;y&gt; &lt;z&gt; &lt;item&gt;</code></td><td>Create item hologram</td></tr>
  <tr><td><code>/hologram list</code></td><td>List all holograms</td></tr>
  <tr><td><code>/hologram remove &lt;id&gt;</code></td><td>Remove a hologram</td></tr>
  <tr><td><code>/hologram clear</code></td><td>Remove all</td></tr>
  <tr><td><code>/hologram save &lt;name&gt;</code></td><td>Export to JSON</td></tr>
  <tr><td><code>/hologram load &lt;name&gt;</code></td><td>Import from JSON</td></tr>
</table>

<hr>

<h2>📦 For Mod Developers</h2>
<p>Add EasyHologram as a dependency to create holograms in your own mod.</p>

<h3>Gradle Setup</h3>
<pre><code>repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    modImplementation 'com.github.tabplus:easyhologram:1.0.0'
}</code></pre>

<h3>Example</h3>
<pre><code>TextHologram hologram = TextHologram.builder(new Vec3d(100, 65, 200))
    .text("§6Welcome!")
    .billboard(true)
    .shadow(true)
    .build(world);

EasyHologram.getManager().register(hologram);</code></pre>

<hr>

<h2>Requirements</h2>
<ul>
  <li>Minecraft <strong>1.21</strong></li>
  <li>Fabric Loader <strong>≥ 0.16.0</strong></li>
  <li>Fabric API</li>
</ul>

<h2>License</h2>
<p><a href="https://opensource.org/licenses/MIT">MIT</a></p>
